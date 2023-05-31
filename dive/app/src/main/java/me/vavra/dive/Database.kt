package me.vavra.dive

import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object Database {
    private val reference = Firebase.database.reference

    fun observeNearbyUsers(): Flow<List<User>> {
        val query = reference.child("nearbyUsers").orderByChild("isVisible").equalTo(true)
        return query.snapshots.map { list ->
            list.children.map { snapshot ->
                User(
                    id = checkNotNull(snapshot.key),
                    name = checkNotNull(snapshot.child("name").getValue<String>()),
                    profilePictureUrl = checkNotNull(
                        snapshot.child("profilePictureUrl").getValue<String>()
                    ),
                    totalRating = checkNotNull(snapshot.child("totalRating").getValue<Double>()),
                )
            }
        }
    }
}