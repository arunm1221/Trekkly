/**
 * Trekkly — delete all documents in the Firestore "treks" collection.
 * Run once before re-seeding with `node seed.js` to get a clean slate
 * (fresh createdAt timestamps, no leftover/orphaned docs).
 *
 * Run from this folder with: node deleteTreks.js
 */

const admin = require("firebase-admin");
const fs = require("fs");
const path = require("path");

const SERVICE_ACCOUNT_PATH = path.join(__dirname, "serviceAccountKey.json");
const COLLECTION = "treks";

if (!fs.existsSync(SERVICE_ACCOUNT_PATH)) {
  console.error("\n❌ serviceAccountKey.json not found in this folder.\n");
  process.exit(1);
}

const serviceAccount = require(SERVICE_ACCOUNT_PATH);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();

async function main() {
  const snapshot = await db.collection(COLLECTION).get();

  if (snapshot.empty) {
    console.log(`\nℹ️  Collection "${COLLECTION}" is already empty. Nothing to delete.\n`);
    process.exit(0);
  }

  console.log(`\n🗑️  Deleting ${snapshot.size} document(s) from "${COLLECTION}"...\n`);

  const batch = db.batch();
  snapshot.docs.forEach((doc) => {
    console.log(`   → ${doc.id}`);
    batch.delete(doc.ref);
  });
  await batch.commit();

  console.log(`\n✅ Done. Deleted ${snapshot.size} document(s) from "${COLLECTION}".\n`);
  process.exit(0);
}

main().catch((err) => {
  console.error("\n❌ Delete failed:", err);
  process.exit(1);
});
