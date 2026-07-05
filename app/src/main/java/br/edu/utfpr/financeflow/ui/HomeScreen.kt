package br.edu.utfpr.financeflow.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToStatement: () -> Unit
) {
    Column(modifier = modifier) {
        Button(
            onClick = onNavigateToStatement
        ) {
            Text("Extrato")
        }
    }
}