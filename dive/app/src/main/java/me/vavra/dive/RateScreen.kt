package me.vavra.dive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation

@Composable
fun RateScreen(loggedInUser: User, rating: Rating) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                loggedInUser.nameVokativ + ",",
                modifier = Modifier.align(CenterHorizontally),
                style = MaterialTheme.typography.headlineLarge
            )
            Text("zde můžeš ohodnotit", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(rating.ofUser.profilePictureUrl)
                    .crossfade(true)
                    .transformations(CircleCropTransformation())
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .align(CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                rating.ofUser.nameAkuzativ,
                modifier = Modifier.align(CenterHorizontally),
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}

@Preview
@Composable
private fun RateScreenPreview() {
    RateScreen(
        loggedInUser = User("", "", "Báro", "", "", 0.0, "", ""),
        rating = Rating(
            User(
                "",
                "",
                "",
                "Davida",
                "https://scontent-prg1-1.xx.fbcdn.net/v/t31.18172-8/15000766_10153993482001156_1656422130253963216_o.jpg?_nc_cat=102&ccb=1-7&_nc_sid=09cbfe&_nc_ohc=RLpldLzCaSwAX8DDndz&_nc_oc=AQmIEdxTMsfYBV7SKeP9Morcrh-oKmZxDcVBB-uHW149aZF8j4ipEj3VHB4YyB3UsTw&_nc_ht=scontent-prg1-1.xx&oh=00_AfBQASctI3P_M0E39kkUjVwA9ABqaZGXTS8DAkdKCX_e5A&oe=64A5A9A9",
                0.0,
                "",
                ""
            )
        )
    )
}
