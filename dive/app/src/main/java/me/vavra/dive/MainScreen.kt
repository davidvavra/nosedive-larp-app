package me.vavra.dive

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation

@Composable
fun MainScreen(
    nearbyUsers: List<User>,
    loggedInUser: User,
    onUserSelected: (User) -> Unit,
    onLoggedOut: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Dive",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = " v okolí", style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.weight(1f))
            LogoutDropDown(loggedInUser, onLoggedOut)
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(all = 20.dp)
        ) {
            items(nearbyUsers) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUserSelected(it) }
                ) {
                    UserRow(it)
                }
            }
        }
    }
}

@Composable
fun LogoutDropDown(loggedInUser: User, onLoggedOut: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box() {
        Row(
            modifier = Modifier.clickable { expanded = !expanded }
        ) {
            UserRow(user = loggedInUser)
        }
        DropdownMenu(
            expanded = expanded,
            modifier = Modifier.align(Alignment.BottomEnd),
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Odhlásit") },
                onClick = onLoggedOut
            )
        }
    }
}

@Composable
fun RowScope.UserRow(user: User) {
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
            .align(Alignment.CenterVertically)
            .padding(start = 16.dp)
    ) {
        Text(text = user.name, style = MaterialTheme.typography.titleMedium)
        Row {
            Text(text = user.mainRating, style = MaterialTheme.typography.titleLarge)
            Text(
                text = user.detailedRating,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(bottom = 2.dp, start = 1.dp)
            )
        }
    }
}