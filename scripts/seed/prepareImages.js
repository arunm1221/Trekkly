/**
 * Trekkly — prepare downloaded images for seeding
 * ------------------------------------------------
 * You download photos yourself and drop them into images/<trek-id>/ with any
 * filenames you like. This script then:
 *
 *   1. Finds the image files in each trek's folder
 *   2. Renames them to cover.jpg / 1.jpg / 2.jpg / 3.jpg …
 *   3. Optionally downsizes + re-encodes them (needs `npm install sharp`)
 *   4. Writes the real filenames back into treks.json
 *   5. Prints a coverage report: which treks still have no photos
 *
 * After this, `node seed.js` uploads everything to Firebase Storage and swaps
 * the filenames for download URLs in Firestore.
 *
 * Run from this folder:
 *   node prepareImages.js            # dry run — shows what it would do
 *   node prepareImages.js --apply    # actually rename files and rewrite treks.json
 *   node prepareImages.js --apply --max-width 1600 --quality 80
 *   node prepareImages.js --apply --format jpg    # if you'd rather have JPEG
 *
 * Output is WebP by default (roughly 30% smaller than JPEG at the same quality,
 * and decoded natively on every Android version this app supports).
 *
 * Which file becomes the cover:
 *   a file whose name starts with "cover", otherwise the first one alphabetically.
 *   Prefix a filename with "0" or "cover" to control it.
 */

const fs = require("fs");
const path = require("path");

// ---- Config ---------------------------------------------------------------

const TREKS_JSON_PATH = path.join(__dirname, "treks.json");
const IMAGES_DIR = path.join(__dirname, "images");
const IMAGE_EXTS = new Set([".jpg", ".jpeg", ".png", ".webp", ".heic", ".avif", ".gif", ".bmp", ".tif", ".tiff"]);

// Source files are moved here rather than deleted, so nothing is ever destroyed.
// Delete these folders yourself once you're happy with the results.
const ORIGINALS = "_originals";

const args = process.argv.slice(2);
const APPLY = args.includes("--apply");
const flag = (name, fallback) => {
  const i = args.indexOf(name);
  return i !== -1 && args[i + 1] ? Number(args[i + 1]) : fallback;
};
const MAX_WIDTH = flag("--max-width", 1600);
const QUALITY = flag("--quality", 80);

// Output format when sharp is available: "webp" (default, ~30% smaller) or "jpg".
// WebP is decoded natively on every Android version this app supports (minSdk 24).
const fmtIndex = args.indexOf("--format");
const FORMAT = fmtIndex !== -1 && args[fmtIndex + 1] === "jpg" ? "jpg" : "webp";

// Re-encode even files that are already in the target format and size.
// Only needed if you change --quality or --max-width and want old files redone.
const FORCE = args.includes("--force-reencode");

// sharp is optional. Without it, files are renamed but not resized or converted.
let sharp = null;
try {
  sharp = require("sharp");
} catch {
  /* not installed */
}

// ---- Helpers --------------------------------------------------------------

const isImage = (f) =>
  IMAGE_EXTS.has(path.extname(f).toLowerCase()) &&
  !f.startsWith("__staging_") && // leftovers from an interrupted earlier run
  !f.startsWith("._"); // macOS resource forks

/** Move a file without ever overwriting an existing one. */
function moveNoClobber(fromAbs, toDirAbs, name) {
  fs.mkdirSync(toDirAbs, { recursive: true });
  const ext = path.extname(name);
  const stem = path.basename(name, ext);
  let dest = path.join(toDirAbs, name);
  let n = 2;
  while (fs.existsSync(dest)) dest = path.join(toDirAbs, `${stem}-${n++}${ext}`);
  fs.renameSync(fromAbs, dest);
  return dest;
}

/** cover first, then everything else alphabetically (natural-ish order). */
function orderFiles(files) {
  const isCover = (f) => path.basename(f).toLowerCase().startsWith("cover");
  const rest = files
    .filter((f) => !isCover(f))
    .sort((a, b) => a.localeCompare(b, undefined, { numeric: true, sensitivity: "base" }));
  return [...files.filter(isCover).sort(), ...rest];
}

/**
 * Produce the final file for one image.
 * With sharp: re-encode to JPEG at MAX_WIDTH / QUALITY.
 * Without:    plain rename, keeping the original extension.
 * Returns the final basename.
 */
async function emit(srcAbs, destStem, dirAbs, reencode = true) {
  // srcAbs already lives in <dirAbs>/_originals/ — it is never deleted.
  if (sharp && reencode) {
    const destAbs = path.join(dirAbs, `${destStem}.${FORMAT}`);
    const pipe = sharp(srcAbs)
      .rotate() // honour EXIF orientation
      .resize({ width: MAX_WIDTH, withoutEnlargement: true });
    await (FORMAT === "webp"
      ? pipe.webp({ quality: QUALITY })
      : pipe.jpeg({ quality: QUALITY, mozjpeg: true })
    ).toFile(destAbs);
    return path.basename(destAbs);
  }

  // Nothing to convert: move the file back out of _originals under its new name.
  const rawExt = path.extname(srcAbs).toLowerCase();
  const ext = rawExt === ".jpeg" ? ".jpg" : rawExt;
  const destAbs = path.join(dirAbs, `${destStem}${ext}`);
  fs.renameSync(srcAbs, destAbs);
  return path.basename(destAbs);
}

/** What emit() will name this file — used so the dry run tells the truth. */
function predictName(srcName, index, reencode = true) {
  const stem = index === 0 ? "cover" : String(index);
  if (sharp && reencode) return `${stem}.${FORMAT}`;
  const ext = path.extname(srcName).toLowerCase();
  return `${stem}${ext === ".jpeg" ? ".jpg" : ext}`;
}

/**
 * Has this file already been through the script? If it's already in the target
 * format and within the width limit, re-encoding it would only lose quality —
 * so it gets a plain rename instead. This is what makes re-runs safe when you
 * top up a folder with new photos later.
 */
async function alreadyProcessed(absPath) {
  if (!sharp || FORCE) return false;
  if (path.extname(absPath).toLowerCase() !== `.${FORMAT}`) return false;
  try {
    const meta = await sharp(absPath).metadata();
    return meta.width <= MAX_WIDTH;
  } catch {
    return false;
  }
}

const kb = (p) => {
  try {
    return Math.round(fs.statSync(p).size / 1024);
  } catch {
    return 0;
  }
};

// ---- Main -----------------------------------------------------------------

async function main() {
  if (!fs.existsSync(TREKS_JSON_PATH)) {
    console.error("\n❌ treks.json not found next to this script.\n");
    process.exit(1);
  }

  const treks = JSON.parse(fs.readFileSync(TREKS_JSON_PATH, "utf8"));

  console.log(`\n🖼  ${APPLY ? "Preparing" : "DRY RUN — inspecting"} images for ${treks.length} trek(s)`);
  console.log(
    sharp
      ? `   sharp found — resizing to ${MAX_WIDTH}px wide, ${FORMAT.toUpperCase()} q${QUALITY}\n`
      : `   sharp not installed — files will be renamed only (run: npm install sharp)\n`
  );

  const empty = [];
  let withPhotos = 0;
  let filesTouched = 0;
  let bytesBefore = 0;
  let bytesAfter = 0;

  for (const trek of treks) {
    const dirAbs = path.join(IMAGES_DIR, trek.id);
    if (!fs.existsSync(dirAbs)) {
      if (APPLY) fs.mkdirSync(dirAbs, { recursive: true });
      empty.push(trek.id);
      continue;
    }

    const found = orderFiles(fs.readdirSync(dirAbs).filter(isImage));
    if (found.length === 0) {
      empty.push(trek.id);
      continue;
    }

    withPhotos++;
    console.log(`→ ${trek.id} (${found.length} file${found.length > 1 ? "s" : ""})`);

    // Decide per file whether it needs re-encoding, before anything is renamed.
    const reencode = [];
    for (const f of found) reencode.push(!(await alreadyProcessed(path.join(dirAbs, f))));

    if (!APPLY) {
      found.forEach((f, i) => {
        const note = reencode[i] ? "" : "   (already processed — rename only)";
        console.log(`     ${f}  →  ${predictName(f, i, reencode[i])}   [${kb(path.join(dirAbs, f))} KB]${note}`);
      });
      continue;
    }

    // Move every source into _originals first, so renaming 1.webp → 2.webp can
    // never clobber a file we still need, and so nothing is ever deleted.
    const origDir = path.join(dirAbs, ORIGINALS);
    const staged = found.map((f) => {
      const from = path.join(dirAbs, f);
      bytesBefore += fs.statSync(from).size;
      return moveNoClobber(from, origDir, f);
    });

    const finalNames = [];
    for (let i = 0; i < staged.length; i++) {
      const stem = i === 0 ? "cover" : String(i);
      const name = await emit(staged[i], stem, dirAbs, reencode[i]);
      finalNames.push(name);
      bytesAfter += fs.statSync(path.join(dirAbs, name)).size;
      filesTouched++;
      const note = reencode[i] ? "" : "  (kept as-is)";
      console.log(`     ✓ ${name}  [${kb(path.join(dirAbs, name))} KB]${note}`);
    }

    // Point the JSON at what is actually on disk.
    trek.coverImage = `${trek.id}/${finalNames[0]}`;
    trek.images = finalNames.slice(1).map((n) => `${trek.id}/${n}`);
  }

  if (APPLY) {
    fs.writeFileSync(TREKS_JSON_PATH, JSON.stringify(treks, null, 2) + "\n", "utf8");
    console.log(`\n📁 Originals kept in images/<trek-id>/${ORIGINALS}/ — delete those folders once you're happy.`);
    console.log(`💾 treks.json updated to match the files on disk.`);
    if (bytesBefore) {
      const mb = (b) => (b / 1024 / 1024).toFixed(1);
      console.log(`   ${filesTouched} image(s) processed — ${mb(bytesBefore)} MB → ${mb(bytesAfter)} MB`);
    }
  }

  console.log(`\n📊 Coverage: ${withPhotos} of ${treks.length} treks have photos.`);
  if (empty.length) {
    console.log(`\n⚠️  ${empty.length} trek(s) still have no images:\n`);
    for (let i = 0; i < empty.length; i += 4) {
      console.log("   " + empty.slice(i, i + 4).join(", "));
    }
    console.log(
      `\n   These will seed with the filename left in place and no Storage upload,\n` +
        `   so the app will show a broken cover until you add photos and re-run.`
    );
  }

  console.log(
    APPLY
      ? `\n✅ Ready. Next: node seed.js\n`
      : `\n👀 Nothing changed. Re-run with --apply to rename files and update treks.json.\n`
  );
}

main().catch((err) => {
  console.error("\n❌ prepareImages failed:", err.message, "\n");
  process.exit(1);
});
