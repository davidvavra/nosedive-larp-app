package me.vavra.dive

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(private val app: Application) : AndroidViewModel(app) {
    var state by mutableStateOf(MainState())
        private set
    private val userId = "bara"
    private val audio = Audio(app)

    init {
        viewModelScope.launch {
            Database.observeNearbyUsers().collect { users ->
                state =
                    MainState(
                        nearbyUsers = users.sortedByDescending { it.totalRating }
                            .filter { it.id != userId },
                        //loggedInUser = users.first { it.id == userId }.shortenName()
                    )
            }
        }
    }

    private fun User.shortenName(): User {
        return this.copy(name = this.name.split(" ")[0])
    }

    fun selectUser(user: User) {
        state = state.copy(rating = Rating(user))
    }

    fun changeRating(stars: Int) {
        audio.play(R.raw.rate)
        state = state.copy(rating = state.rating?.copy(stars = stars))
    }

    fun sendRating() {
        audio.play(R.raw.swoosh)
        val rating = state.rating
        if (rating != null) {
            Database.addRating(userId, rating.ofUser.id, rating.stars)
        }
        state = state.copy(rating = null)
    }

    fun closeRating() {
        state = state.copy(rating = null)
    }
}