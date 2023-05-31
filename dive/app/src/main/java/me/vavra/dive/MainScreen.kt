package me.vavra.dive

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MainScreen(viewModel: MainViewModel) {
    LazyColumn {
        items(viewModel.state.nearbyUsers) {
            UserRow(it)
        }
    }
}

@Composable
fun UserRow(user: User) {
    Text(text = user.name + "|" + user.profilePictureUrl + "|" + user.totalRating)
}