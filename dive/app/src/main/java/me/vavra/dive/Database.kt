package me.vavra.dive

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.RoundingMode
import java.text.DecimalFormat

object Database {
    init {
        Firebase.database.setPersistenceEnabled(true)
    }

    private val reference = Firebase.database.reference
    private val ratingFormat =
        DecimalFormat("0.000").apply { this.roundingMode = RoundingMode.HALF_UP }

    fun observeNearbyUsers(): Flow<List<User>> {
        val query = reference.child("nearbyUsers")
        return query.snapshots.map { list ->
            list.children.map { snapshot ->
                val totalRating = snapshot.child("totalRating").getValue<Double>() ?: 0.0
                User(
                    id = checkNotNull(snapshot.key),
                    name = checkNotNull(snapshot.child("name").getValue<String>()),
                    nameVokativ = snapshot.child("nameVokativ").getValue<String>() ?: "",
                    nameAkuzativ = snapshot.child("nameAkuzativ").getValue<String>() ?: "",
                    nameGenitiv = snapshot.child("nameGenitiv").getValue<String>() ?: "",
                    profilePictureUrl = checkNotNull(
                        snapshot.child("profilePictureUrl").getValue<String>()
                    ),
                    totalRating = totalRating,
                    mainRating = totalRating.formatToOnceDecimal(),
                    detailedRating = totalRating.extractThirdAndFourthDecimal(),
                    isVisible = checkNotNull(snapshot.child("isVisible").getValue<Boolean>())
                )
            }
        }
    }

    fun addRating(
        app: Application,
        from: String,
        to: String,
        stars: Int,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        if (isOnline(app)) {
            reference.child("ratings").push().updateChildren(
                hashMapOf(
                    "from" to from,
                    "to" to to,
                    "stars" to stars,
                    "createdAt" to ServerValue.TIMESTAMP
                )
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    Log.e("Dive", "Failed to send rating", it.exception)
                    onFail()
                }
            }
        } else {
            Log.w("Dive", "offline")
            onFail()
        }
    }

    fun updateNotificationsToken(token: String) {
        val uid = Firebase.auth.uid
        if (uid != null) {
            Log.d("FCM token", token)
            reference.child("userSecrets").child(uid).updateChildren(
                hashMapOf(
                    "notificationsToken" to token
                ) as Map<String, Any>
            )
        }
    }

    private fun Double.formatToOnceDecimal(): String {
        val formatted = ratingFormat.format(this)
        return formatted.substring(0, 3)
    }

    private fun Double.extractThirdAndFourthDecimal(): String {
        val formatted = ratingFormat.format(this)
        return formatted.substring(3, 5)
    }

    private fun isOnline(app: Application): Boolean {
        val connectivityManager = app.getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
        return caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ?: false
    }
}