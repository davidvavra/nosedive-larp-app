import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import { doLogin } from './login';
import { doProcessRating, doProcessReport } from './database';

admin.initializeApp({
    databaseURL: "https://nosedive-larp-default-rtdb.europe-west1.firebasedatabase.app"
});

export let login = functions.region('europe-west1').https.onRequest(async (request, response) => {
    await doLogin(request.query["password"] as string, response)
})

export let processRating = functions.region('europe-west1').database.ref("ratings/{ratingId}").onCreate(async (snap, context) => {
    await doProcessRating(snap)
})

export let processReport = functions.region('europe-west1').database.ref("reports/{reportId}").onCreate(async (snap, context) => {
    await doProcessReport(snap)
})