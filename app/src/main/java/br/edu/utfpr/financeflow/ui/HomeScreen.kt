package br.edu.utfpr.financeflow.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.financeflow.utils.CurrencyVisualTransformation
import br.edu.utfpr.financeflow.utils.DateVisualTransformation
import br.edu.utfpr.financeflow.viewmodel.HomeViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onNavigateToStatement: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(value = false) }

    Column(modifier = modifier) {

        OutlinedTextField(
            value = viewModel.description,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = { Text("Descrição") },
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
        )

        OutlinedTextField(
            value = viewModel.amount,
            onValueChange = { viewModel.onAmountChange(it) },
            label = { Text("Valor") },
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            visualTransformation = CurrencyVisualTransformation()
        )

        OutlinedTextField(
            value = viewModel.dateString,
            onValueChange = { viewModel.onDateStringChange(it) },
            label = { Text("Data") },
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            visualTransformation = DateVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Selecionar data")
                }
            }
        )

        if (showDatePicker) {
            DatePickerModal(
                initialDate = viewModel.date,
                onDateSelected = { millis ->
                    millis?.let {
                        val selectedDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        viewModel.onDateChange(selectedDate)
                    }
                },
                onDismiss = { showDatePicker = false }
            )
        }

        Button(
            onClick = onNavigateToStatement,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Extrato")
        }
    }
}

@Composable
fun DatePickerModal(
    initialDate: LocalDate,
    onDateSelected: (Long?) -> Unit, 
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    )

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState.selectedDateMillis)
            onDismiss()
        }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancelar")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}