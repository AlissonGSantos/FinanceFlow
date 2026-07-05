package br.edu.utfpr.financeflow.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun StatementScreen(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Oi, esse é o seu extrato :)",
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}