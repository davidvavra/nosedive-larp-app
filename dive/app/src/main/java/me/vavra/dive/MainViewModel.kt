package me.vavra.dive

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var state by mutableStateOf(MainState(listOf()))
        private set

    init {
        viewModelScope.launch {
            state = MainState(listOf(User("david", "destil.cz", 2.4)))
        }
    }
}