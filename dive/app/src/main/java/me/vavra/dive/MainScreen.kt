package me.vavra.dive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(all = 20.dp)
    ) {
        items(viewModel.state.nearbyUsers) {
            UserRow(it)
        }
    }
}

@Composable
fun UserRow(user: User) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.profilePictureUrl)
                .crossfade(true)
                .transformations(CircleCropTransformation())
                .build(),
            contentDescription = null,
            modifier = Modifier.size(60.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
                .padding(start = 20.dp)
        ) {
            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
            Row() {
                Text(text = user.mainRating, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = user.detailedRating,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.Bottom)
                        .padding(bottom = 2.dp, start = 1.dp)
                )
            }
        }
    }
}