import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import { doLogin } from './login';

admin.initializeApp({
    databaseURL: "https://nosedive-larp-default-rtdb.europe-west1.firebasedatabase.app"
});

export let login = functions.region('europe-west1').https.onRequest(async (request, response) => {
    await doLogin(request.query["password"] as string, response)
})