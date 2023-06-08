package me.vavra.dive

import android.util.JsonReader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object Auth {

    fun observeUserId(): Flow<String?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.uid)
        }
        Firebase.auth.addAuthStateListener(authStateListener)
        awaitClose {
            Firebase.auth.removeAuthStateListener(authStateListener)
        }
    }

    suspend fun login(password: String) {
        withContext(Dispatchers.IO) {
            try {
                val url =
                    URL("https://europe-west1-nosedive-larp.cloudfunctions.net/login?password=$password")
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                if (connection.responseCode == 200) {
                    val reader = JsonReader(connection.inputStream.reader())
                    reader.beginObject()
                    reader.nextName()
                    val token = reader.nextString()
                    reader.nextName()
                    val invalidPassword = reader.nextBoolean()
                    if (!invalidPassword) {
                        Firebase.auth.signInWithCustomToken(token).await()
                    }
                    reader.endObject()
                    reader.close()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun logout() {
        Firebase.auth.signOut()
    }
}