package com.example.todoapp.ui.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.ui.theme.ToDoAppTheme

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    onUseOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.error_connection),
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(text = stringResource(id = R.string.retry))
            }
            Button(
                onClick = onUseOffline,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(text = stringResource(id = R.string.use_offline))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenLightPreview() {
    ToDoAppTheme(darkTheme = false) {
        ErrorScreen(
            message = "Нет подключения к интернету",
            onRetry = {},
            onUseOffline = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF161618) // md_theme_dark_background_primary
@Composable
private fun ErrorScreenDarkPreview() {
    ToDoAppTheme(darkTheme = true) {
        ErrorScreen(
            message = "Нет подключения к интернету",
            onRetry = {},
            onUseOffline = {}
        )
    }
}