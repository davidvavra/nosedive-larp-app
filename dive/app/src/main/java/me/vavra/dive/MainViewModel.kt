package me.vavra.dive

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    var state by mutableStateOf(MainState())
        private set
    private val audio = Audio(app)

    init {
        viewModelScope.launch {
            Auth.observeUserId().flatMapLatest { userId ->
                if (userId == null) {
                    flowOf(MainState())
                } else {
                    Database.observeNearbyUsers().map { users ->
                        MainState(
                            nearbyUsers = users.sortedByDescending { it.totalRating }
                                .filter { it.id != userId },
                            loggedInUser = users.first { it.id == userId }.shortenName()
                        )
                    }
                }
            }.collect {
                state = it
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
        val loggedInUser = state.loggedInUser
        if (rating != null && loggedInUser != null) {
            Database.addRating(loggedInUser.id, rating.ofUser.id, rating.stars)
        }
        state = state.copy(rating = null)
    }

    fun closeRating() {
        state = state.copy(rating = null)
    }

    fun login(password: String) {
        state = state.copy(loggingIn = true)
        viewModelScope.launch {
            Auth.login(password)
            state = state.copy(loggingIn = false)
        }
    }

    fun logOut() {
        Auth.logout()
    }
}