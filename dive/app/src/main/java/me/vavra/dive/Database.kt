package me.vavra.dive

import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.RoundingMode
import java.text.DecimalFormat

object Database {
    private val reference = Firebase.database.reference
    private val mainRatingFormat =
        DecimalFormat("0.0").apply { this.roundingMode = RoundingMode.DOWN }
    private val detailedRatingFormat =
        DecimalFormat("0.000").apply { this.roundingMode = RoundingMode.HALF_UP }

    fun observeNearbyUsers(): Flow<List<User>> {
        val query = reference.child("nearbyUsers").orderByChild("isVisible").equalTo(true)
        return query.snapshots.map { list ->
            list.children.map { snapshot ->
                val totalRating = checkNotNull(snapshot.child("totalRating").getValue<Double>())
                User(
                    id = checkNotNull(snapshot.key),
                    name = checkNotNull(snapshot.child("name").getValue<String>()),
                    profilePictureUrl = checkNotNull(
                        snapshot.child("profilePictureUrl").getValue<String>()
                    ),
                    mainRating = totalRating.formatToOnceDecimal(),
                    detailedRating = totalRating.extractThirdAndFourthDecimal()
                )
            }
        }
    }

    private fun Double.formatToOnceDecimal(): String {
        return mainRatingFormat.format(this)
    }

    private fun Double.extractThirdAndFourthDecimal(): String {
        val mainRating = mainRatingFormat.format(this)
        val detailedRating = detailedRatingFormat.format(this)
        return detailedRating.replace(mainRating, "")
    }
}