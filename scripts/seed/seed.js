/**
 * Trekkly — Firebase seed script
 * --------------------------------
 * Reads treks.json, uploads each trek's images to Firebase Storage,
 * swaps the local filenames for public download URLs, then writes
 * every trek as a document into the Firestore "treks" collection.
 *
 * Run it once from this folder with:   node seed.js
 *
 * Prerequisites (see README.md for details):
 *   1. Node.js installed
 *   2. `npm install` run in this folder (installs firebase-admin)
 *   3. serviceAccountKey.json present in this folder
 *   4. An ./images folder with the image files referenced in treks.json
 */

const admin = require("firebase-admin");
const fs = require("fs");
const path = require("path");
const crypto = require("crypto");

// ---- Config ---------------------------------------------------------------

const SERVICE_ACCOUNT_PATH = path.join(__dirname, "serviceAccountKey.json");
const TREKS_JSON_PATH = path.join(__dirname, "treks.json");
const IMAGES_DIR = path.join(__dirname, "images");
const STORAGE_BUCKET = "trekkly-6652a.firebasestorage.app"; // from google-services.json
const COLLECTION = "treks";

// ---- Initialise the Admin SDK --------------------------------------------

if (!fs.existsSync(SERVICE_ACCOUNT_PATH)) {
  console.error(
    "\n❌ serviceAccountKey.json not found in this folder.\n" +
      "   Download it from Firebase Console → Project Settings → Service Accounts → Generate new private key,\n" +
      "   rename it to serviceAccountKey.json and place it next to seed.js.\n"
  );
  process.exit(1);
}

const serviceAccount = require(SERVICE_ACCOUNT_PATH);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  storageBucket: STORAGE_BUCKET,
});

const db = admin.firestore();
const bucket = admin.storage().bucket();

// ---- Helpers --------------------------------------------------------------

/**
 * Uploads one local image to Storage and returns its public URL.
 * If the local file does not exist, the value is returned unchanged
 * (so you can also hand-write full URLs in treks.json if you prefer).
 */
async function uploadImage(trekId, relativePath) {
  // Already a URL? Leave it as-is.
  if (/^https?:\/\//i.test(relativePath)) return relativePath;

  const localFile = path.join(IMAGES_DIR, relativePath);
  if (!fs.existsSync(localFile)) {
    console.warn(`   ⚠️  Image not found, skipping upload: ${relativePath}`);
    return relativePath; // keep filename; you can re-run later once it exists
  }

  const destination = `treks/${relativePath}`; // e.g. treks/kedarkantha/cover.jpg

  // A download token lets the app read the file via a tokenized URL,
  // which works regardless of bucket access mode or security rules.
  const token = crypto.randomUUID();
  await bucket.upload(localFile, {
    destination,
    metadata: {
      cacheControl: "public, max-age=31536000",
      metadata: { firebaseStorageDownloadTokens: token },
    },
  });

  // Firebase-style download URL (carries its own access token).
  const url = `https://firebasestorage.googleapis.com/v0/b/${STORAGE_BUCKET}/o/${encodeURIComponent(
    destination
  )}?alt=media&token=${token}`;
  console.log(`   ↑ uploaded ${relativePath}`);
  return url;
}

/** Uploads cover + gallery images for a trek and returns the updated object. */
async function processImages(trek) {
  if (trek.coverImage) {
    trek.coverImage = await uploadImage(trek.id, trek.coverImage);
  }
  if (Array.isArray(trek.images)) {
    const urls = [];
    for (const img of trek.images) {
      urls.push(await uploadImage(trek.id, img));
    }
    trek.images = urls;
  }
  return trek;
}

// ---- Main -----------------------------------------------------------------

async function main() {
  const treks = JSON.parse(fs.readFileSync(TREKS_JSON_PATH, "utf8"));
  console.log(`\n🌱 Seeding ${treks.length} trek(s) to Firestore "${COLLECTION}"...\n`);

  for (const trek of treks) {
    if (!trek.id) {
      console.warn("   ⚠️  Skipping a trek with no 'id'.");
      continue;
    }
    console.log(`→ ${trek.id} (${trek.title})`);

    // 1. Upload images and swap filenames for URLs.
    await processImages(trek);

    // 2. Handle timestamps: keep original createdAt, always refresh updatedAt.
    const docRef = db.collection(COLLECTION).doc(trek.id);
    const existing = await docRef.get();
    const now = admin.firestore.FieldValue.serverTimestamp();

    trek.updatedAt = now;
    trek.createdAt =
      existing.exists && existing.data().createdAt
        ? existing.data().createdAt
        : now;

    // 3. Write the document (full overwrite of the data fields).
    await docRef.set(trek);
    console.log(`   ✓ written to Firestore\n`);
  }

  console.log("✅ Done. All treks seeded.\n");
  process.exit(0);
}

main().catch((err) => {
  console.error("\n❌ Seeding failed:", err);
  process.exit(1);
});
