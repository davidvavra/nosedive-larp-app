package me.vavra.dive

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation

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
    Row(modifier = Modifier.padding(horizontal = 20.dp).padding(top = 20.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.profilePictureUrl)
                .crossfade(true)
                .transformations(CircleCropTransformation())
                .build(),
            contentDescription = null,
            modifier = Modifier.size(60.dp)
        )
        Column(modifier = Modifier.padding(start = 20.dp).fillMaxHeight().align(Alignment.CenterVertically)) {
            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
            Text(text = user.totalRating.toString(), style = MaterialTheme.typography.titleLarge)
        }
    }
}