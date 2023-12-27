package me.vavra.dive

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import me.vavra.dive.ui.theme.Rate

@Composable
fun RatingFailedScreen(
    rating: Rating,
    onClose: () -> Unit
) {
    var swipeOffset by remember { mutableStateOf(0f) }
    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectVerticalDragGestures { change, dragAmount ->
                change.consume()
                swipeOffset += dragAmount
                if (swipeOffset > 150) {
                    onClose()
                } else if (swipeOffset < -150) {
                    onClose()
                }
            }

        }) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                "Hodnocení",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(CenterHorizontally)
            )
            Text(
                rating.ofUser.nameGenitiv,
                modifier = Modifier.align(CenterHorizontally),
                style = MaterialTheme.typography.displayMedium
            )
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
            Spacer(modifier = Modifier.height(26.dp))
            Text(
                "se nepodařilo odeslat.",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(CenterHorizontally)
            )
            Text(
                "Zkontrolujte připojení.",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(CenterHorizontally)
            )
        }
    }
}

@Preview
@Composable
private fun RatingFailedPreview() {
    RatingFailedScreen(
        rating = Rating(
            User(
                "",
                "",
                "",
                "Davida",
                nameGenitiv = "Davida",
                "https://firebasestorage.googleapis.com/v0/b/nosedive-larp.appspot.com/o/profile_pics%2FAuditor%20Va%CC%81clav%20Svoboda.jpg?alt=media&token=11df83cb-200c-4c85-a9c5-f7921d401412",
                0.0,
                "",
                "", true
            )
        ), {}
    )
}
