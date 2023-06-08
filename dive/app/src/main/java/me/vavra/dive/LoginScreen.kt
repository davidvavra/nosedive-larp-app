package me.vavra.dive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.vavra.dive.ui.theme.DiveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    logingIn: Boolean,
    onLogin: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(
                    text = "Dive",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = " přihlášení", style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            var password by remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                value = password,
                label = { Text("Zadejte heslo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { newText ->
                    password = newText
                }
            )
            Spacer(modifier = Modifier.height(32.dp))
            if (logingIn) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = { onLogin(password.text) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Přihlásit se")
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    DiveTheme {
        LoginScreen(false, onLogin = {})
    }
}
