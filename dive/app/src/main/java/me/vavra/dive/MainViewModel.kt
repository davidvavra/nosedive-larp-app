package me.vavra.dive

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var state by mutableStateOf(MainState(listOf()))
        private set

    init {
        viewModelScope.launch {
            Database.observeNearbyUsers().collect { users ->
                state = MainState(users.sortedBy { it.name })
            }
        }
    }
}