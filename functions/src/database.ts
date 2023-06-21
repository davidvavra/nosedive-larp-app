import admin = require("firebase-admin");
import { DataSnapshot } from "firebase-functions/lib/v1/providers/database";

export async function doProcessRating(snap: DataSnapshot) {
  const rating = snap.val();
  const raterUser = (await admin.database().ref("nearbyUsers/" + rating.from).once("value")).val()
  // change rating
  const raterRating = raterUser.totalRating
  const weight = raterRating < 1 ? 0 : raterRating < 2 ? 1 : raterRating < 3 ? 2 : raterRating < 4 ? 3 : raterRating < 4.5 ? 4 : 5
  // const ratedUser = (await admin.database().ref("nearbyUsers/" + rating.to).once("value")).val()
  // const newRatingCount = ratedUser.ratingCount + weight
  // const newRating = (ratedUser.totalRating * ratedUser.ratingCount + rating.stars * weight) / newRatingCount
  // await admin.database().ref("nearbyUsers/" + rating.to).update({
  //   'ratingCount': newRatingCount,
  //   'totalRating': newRating
  // })
  await admin.database().ref("nearbyUsers/" + rating.to).transaction(
    ratedUser => {
      if (ratedUser == null) return null
      const newRatingCount = ratedUser.ratingCount + weight
      ratedUser.totalRating = (ratedUser.totalRating * ratedUser.ratingCount + rating.stars * weight) / newRatingCount
      ratedUser.ratingCount = newRatingCount
      return ratedUser
    }
  )
  // send notification
  const token = (await admin.database().ref("userSecrets/" + rating.to + "/notificationsToken").once("value")).val()
  const androidConfig: admin.messaging.AndroidConfig = {
    priority: 'high'
  }
  const message = {
    data: {
      fromNameGenitiv: raterUser.nameGenitiv,
      stars: String(rating.stars)
    },
    android: androidConfig,
    token: token
  };
  console.log("message=" + JSON.stringify(message))
  await admin.messaging().send(message)
}