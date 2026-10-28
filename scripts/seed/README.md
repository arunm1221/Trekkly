# Trekkly Seed Script

This is a small one-off tool that runs **on your computer** (not inside the Android app)
to fill your Firebase project with trek data. It uploads images to **Firebase Storage**
and writes trek documents into **Firestore**.

You run it whenever you add or change treks in `treks.json`.

---

## What's in this folder

| File                 | What it is                                                        |
| -------------------- | ----------------------------------------------------------------- |
| `treks.json`         | Your trek data. Edit this to add/change treks.                    |
| `seed.js`            | The script that uploads images and writes to Firebase.            |
| `package.json`       | Lists the one dependency (`firebase-admin`).                      |
| `.gitignore`         | Keeps your secret key and images out of git.                      |
| `images/`            | (You create this) The actual image files. See below.             |
| `serviceAccountKey.json` | (You add this) Your secret admin key. **Never commit it.**    |

---

## One-time setup

### 1. Install Node.js
Download and install from <https://nodejs.org> (the LTS version is fine).
Check it worked by running in a terminal:
```bash
node --version
```

### 2. Get your service account key
This is the secret file that lets the script write to Firebase as an admin.

1. Go to the [Firebase Console](https://console.firebase.google.com/) → project **trekkly-6652a**.
2. Click the gear icon → **Project settings** → **Service accounts** tab.
3. Click **Generate new private key** → confirm. A JSON file downloads.
4. Rename it to **`serviceAccountKey.json`** and place it in this folder (next to `seed.js`).

> ⚠️ Treat this file like a password. Never commit it to git or ship it in the app.
> It's already listed in `.gitignore`.

### 3. Install the dependency
In a terminal, navigate to this folder and run:
```bash
cd scripts/seed
npm install
```
This downloads `firebase-admin` into a `node_modules/` folder.

---

## Adding your trek images

Inside this folder, create an `images/` folder. Put each trek's images in a
subfolder, matching the paths used in `treks.json`. For example, `treks.json` has:

```json
"coverImage": "kedarkantha/cover.jpg",
"images": ["kedarkantha/1.jpg", "kedarkantha/2.jpg", "kedarkantha/3.jpg"]
```

So your folder should look like:

```
images/
  kedarkantha/
    cover.jpg
    1.jpg
    2.jpg
    3.jpg
  chembra-peak/
    cover.jpg
    1.jpg
    2.jpg
```

> If an image file is missing, the script skips it (with a warning) and keeps the
> filename. You can add the file and re-run later — only the listed treks are touched.
> You can also paste a full `https://...` URL directly into `treks.json` and the
> script will leave it untouched.

---

## Running the script

From this folder:
```bash
node seed.js
```
or
```bash
npm run seed
```

You'll see progress printed for each trek:
```
🌱 Seeding 2 trek(s) to Firestore "treks"...

→ kedarkantha (Kedarkantha Trek)
   ↑ uploaded kedarkantha/cover.jpg
   ↑ uploaded kedarkantha/1.jpg
   ✓ written to Firestore

✅ Done. All treks seeded.
```

Open the Firebase Console → Firestore to see your `treks` collection populated,
and Storage to see the uploaded images.

---

## How to add a new trek later

1. Add the image files under `images/<your-trek-id>/`.
2. Add a new object to the array in `treks.json` (copy an existing one as a template).
3. Run `node seed.js` again.

Re-running is safe: it overwrites each trek's data with the latest from `treks.json`,
keeps the original `createdAt`, and refreshes `updatedAt`. That `updatedAt` field is
what the app uses for efficient incremental sync into Room.

---

## The data shape (what each trek field means)

| Field             | Type                | Notes                                                     |
| ----------------- | ------------------- | -------------------------------------------------------- |
| `id`              | string              | Unique, used as the Firestore document id.               |
| `title`           | string              |                                                          |
| `coverImage`      | string (URL)        | Thumbnail for the home-screen card.                      |
| `images`          | string[] (URLs)     | Gallery for the detail screen.                           |
| `shortDescription`| string              | One line for the card.                                   |
| `description`     | string              | Full text for the detail screen.                         |
| `difficultyLevel` | string              | One of: Easy, Moderate, Difficult, Expert.               |
| `altitude`        | number              | Max altitude in metres.                                  |
| `distance`        | number              | Total distance in km.                                    |
| `days` / `nights` | number              | Trip length.                                             |
| `bestSeason`      | string              |                                                          |
| `location`        | string              | Region / state / country.                                |
| `latitude` / `longitude` | number       | For maps.                                                |
| `startingPoint` / `endingPoint` | string |                                                       |
| `trekType`        | string              | e.g. "Day trek", "Multi-day".                            |
| `rating`          | number              | Average rating.                                          |
| `reviewCount`     | number              |                                                          |
| `tags`            | string[]            | For filtering/search.                                     |
| `isFeatured` / `isPopular` | boolean    | Drive home-screen sections.                              |
| `itinerary`       | object[]            | Day objects: `dayNumber, title, description, distanceCovered, altitudeReached`. |
| `createdAt` / `updatedAt` | timestamp   | Set automatically by the script. Don't edit by hand.     |

> Note: `isFavorite` is **not** here on purpose. It's local-only user state stored
> in Room (in a separate favorites table), so it survives every sync.
