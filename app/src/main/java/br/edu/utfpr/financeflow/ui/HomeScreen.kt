package br.edu.utfpr.financeflow.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.financeflow.FinanceFlowApplication
import br.edu.utfpr.financeflow.R
import br.edu.utfpr.financeflow.model.EntryType
import br.edu.utfpr.financeflow.utils.CurrencyVisualTransformation
import br.edu.utfpr.financeflow.utils.DateVisualTransformation
import br.edu.utfpr.financeflow.viewmodel.HomeViewModel
import br.edu.utfpr.financeflow.viewmodel.HomeViewModelFactory
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToStatement: () -> Unit,
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as FinanceFlowApplication).repository
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    Column(modifier = modifier) {

        OutlinedTextField(
            value = viewModel.description,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = { Text(stringResource(R.string.description)) },
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
        )

        OutlinedTextField(
            value = viewModel.amount,
            onValueChange = { viewModel.onAmountChange(it) },
            label = { Text(stringResource(R.string.amount)) },
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
            label = { Text(stringResource(R.string.date)) },
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            visualTransformation = DateVisualTransformation(),
            isError = !viewModel.isDateValid,
            supportingText = {
                if (!viewModel.isDateValid) {
                    Text(
                        text = stringResource(R.string.invalid_date),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = { viewModel.onShowDatePickerChange(true) }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.select_date)
                    )
                }
            })

        if (viewModel.showDatePicker) {
            DatePickerModal(initialDate = viewModel.date, onDateSelected = { millis ->
                millis?.let {
                    val selectedDate =
                        Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                    viewModel.onDateChange(selectedDate)
                }
            }, onDismiss = { viewModel.onShowDatePickerChange(false) })
        }

        Column(modifier.selectableGroup()) {
            EntryType.entries.forEach { entryType ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (entryType == viewModel.entryType),
                            onClick = { viewModel.onEntryTypeChange(entryType) })
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (entryType == viewModel.entryType),
                        onClick = { viewModel.onEntryTypeChange(entryType) })
                    Text(
                        text = stringResource(entryType.label),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )

                }
            }
        }

        Button(
            onClick = { viewModel.saveEntry() }, modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.save))
        }

        Button(
            onClick = onNavigateToStatement, modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
        ) {
            Text(stringResource(R.string.statement))
        }
    }
}

@Composable
fun DatePickerModal(
    initialDate: LocalDate, onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.of("UTC")).toInstant()
            .toEpochMilli()
    )

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState.selectedDateMillis)
            onDismiss()
        }) {
            Text(stringResource(R.string.ok))
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.cancel))
        }
    }) {
        DatePicker(state = datePickerState)
    }
}