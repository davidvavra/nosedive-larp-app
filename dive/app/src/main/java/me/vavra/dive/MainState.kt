package me.vavra.dive

data class MainState(
    val nearbyUsers: List<User> = listOf(),
    val loggedInUser: User = User("", "", "", 0.0, "", "")
)

data class User(
    val id: String,
    val name: String,
    val profilePictureUrl: String,
    val totalRating: Double,
    val mainRating: String,
    val detailedRating: String
)