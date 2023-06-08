package me.vavra.dive

data class MainState(
    val nearbyUsers: List<User> = listOf(),
    val loggingIn: Boolean = false,
    val loggedInUser: User? = null,
    val rating: Rating? = null
)

data class User(
    val id: String,
    val name: String,
    val nameVokativ: String,
    val nameAkuzativ: String,
    val profilePictureUrl: String,
    val totalRating: Double,
    val mainRating: String,
    val detailedRating: String
)

data class Rating(
    val ofUser: User,
    val stars: Int = 0
)