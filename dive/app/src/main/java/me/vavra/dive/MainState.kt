package me.vavra.dive

data class MainState(
    val nearbyUsers: List<User>
)

data class User(
    val name: String,
    val profilePictureUrl: String,
    val totalRating: Double
)