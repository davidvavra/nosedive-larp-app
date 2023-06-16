import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

export async function doLogin(password: string, response: functions.Response) {
    let delegates = (await admin.database().ref("userSecrets").orderByChild("password").equalTo(password).once("value")).val()
    if (delegates == null) {
        await delay(2000)
        sendResponse(new LoginResponse("", true), response)
    } else {
        let uid = Object.keys(delegates)[0]
        let token = await admin.auth().createCustomToken(uid)
        sendResponse(new LoginResponse(token, false), response)
    }
}

function delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function sendResponse(loginResponse: LoginResponse, http: functions.Response) {
    http.setHeader('Access-Control-Allow-Origin', '*');
    http.setHeader('Access-Control-Request-Method', '*');
    http.setHeader('Access-Control-Allow-Headers', '*');
    http.writeHead(200)
    http.write(JSON.stringify(loginResponse))
    http.end()
}

class LoginResponse {
    constructor(public token: string, public invalidPassword: boolean) { }
}