package me.vavra.dive

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.vavra.dive.Database.updateNotificationsToken

class MainViewModel(private val app: Application) : AndroidViewModel(app) {
    var state by mutableStateOf(MainState())
        private set
    private val audio = Audio(app)

    init {
        viewModelScope.launch {
            Auth.observeUserId().flatMapLatest { userId ->
                if (userId == null) {
                    flowOf(state.copy(loggedInUser = null))
                } else {
                    Database.observeNearbyUsers().map { users ->
                        state.copy(
                            nearbyUsers = users.sortedByDescending { it.totalRating }
                                .filter { it.isVisible && it.id != userId },
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
        val rating = state.rating
        val sent = rating?.sent ?: false
        if ((rating?.stars ?: 0) > 0 && !sent) {
            val loggedInUser = state.loggedInUser
            if (rating != null && loggedInUser != null) {
                Database.addRating(
                    app,
                    loggedInUser.id,
                    rating.ofUser.id,
                    rating.stars,
                    onSuccess = {
                        audio.play(R.raw.swoosh)
                        state = state.copy(rating = rating.copy(success = true))
                    },
                    onFail = {
                        audio.play(R.raw.error)
                        state = state.copy(rating = rating.copy(fail = true))
                    })
                state = state.copy(rating = state.rating?.copy(sent = true))
            }
        }
    }

    fun closeRating() {
        state = state.copy(rating = null)
    }

    fun login(password: String) {
        state = state.copy(loggingIn = true)
        viewModelScope.launch {
            Auth.login(password)
            updateNotificationsToken()
            state = state.copy(loggingIn = false)
        }
    }

    private suspend fun updateNotificationsToken() {
        val token = FirebaseMessaging.getInstance().token.await()
        Database.updateNotificationsToken(token)
    }

    fun logOut() {
        Auth.logout()
    }
}