package me.vavra.dive

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var state by mutableStateOf(MainState())
        private set
    private val userId = "bara"

    init {
        viewModelScope.launch {
            Database.observeNearbyUsers().collect { users ->
                state =
                    MainState(
                        nearbyUsers = users.sortedByDescending { it.totalRating }
                            .filter { it.id != userId },
                        loggedInUser = users.first { it.id == userId }.shortenName()
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
}