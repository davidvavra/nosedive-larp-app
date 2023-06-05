package me.vavra.dive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import me.vavra.dive.ui.theme.DiveTheme
import me.vavra.dive.ui.theme.Nosedive1
import me.vavra.dive.ui.theme.Nosedive2

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiveTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Nosedive1, Nosedive2)
                            )
                        )
                ) {
                    val state = viewModel.state
                    if (state.rating == null) {
                        MainScreen(state, onUserSelected = {
                            viewModel.selectUser(it)
                        })
                    } else {
                        RateScreen(state.loggedInUser, state.rating)
                    }
                }
            }
        }
    }
}
