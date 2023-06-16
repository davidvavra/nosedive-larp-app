import admin = require("firebase-admin");
import { DataSnapshot } from "firebase-functions/lib/v1/providers/database";

export async function doProcessRating(snap: DataSnapshot) {
  const rating = snap.val();
  const fromNameGenitiv = (await admin.database().ref("nearbyUsers/" + rating.from + "/nameGenitiv").once("value")).val()
  const token = (await admin.database().ref("userSecrets/" + rating.from + "/notificationsToken").once("value")).val()
  const androidConfig: admin.messaging.AndroidConfig = {
    priority: 'high'
  }
  const message = {
    data: {
      fromNameGenitiv: fromNameGenitiv,
      stars: String(rating.stars)
    },
    android: androidConfig,
    token: token
  };
  console.log("message=" + JSON.stringify(message))
  await admin.messaging().send(message)
}