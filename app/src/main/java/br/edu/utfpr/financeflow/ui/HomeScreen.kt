package br.edu.utfpr.financeflow.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.financeflow.FinanceFlowApplication
import br.edu.utfpr.financeflow.R
import br.edu.utfpr.financeflow.model.EntryType
import br.edu.utfpr.financeflow.ui.theme.ExpenseColor
import br.edu.utfpr.financeflow.ui.theme.ExpenseContainerColor
import br.edu.utfpr.financeflow.ui.theme.IncomeColor
import br.edu.utfpr.financeflow.ui.theme.IncomeContainerColor
import br.edu.utfpr.financeflow.utils.CurrencyVisualTransformation
import br.edu.utfpr.financeflow.utils.DateVisualTransformation
import br.edu.utfpr.financeflow.viewmodel.HomeUiEvent
import br.edu.utfpr.financeflow.viewmodel.HomeViewModel
import br.edu.utfpr.financeflow.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToStatement: () -> Unit,
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as FinanceFlowApplication).repository
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    val snackbarHostState = remember { SnackbarHostState() }
    val entrySavedMessage = stringResource(R.string.entry_saved_success)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is HomeUiEvent.EntrySaved -> {
                    snackbarHostState.showSnackbar(entrySavedMessage)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_name), fontWeight = FontWeight.Bold
                    )
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Nova Entrada",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = viewModel.description,
                        onValueChange = { viewModel.onDescriptionChange(it) },
                        label = { Text(stringResource(R.string.description)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = !viewModel.isDescriptionValid,
                        supportingText = {
                            if (!viewModel.isDescriptionValid) {
                                Text(
                                    text = stringResource(R.string.description_required),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Description, contentDescription = null
                            )
                        })

                    OutlinedTextField(
                        value = viewModel.amount,
                        onValueChange = { viewModel.onAmountChange(it) },
                        label = { Text(stringResource(R.string.amount)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        visualTransformation = CurrencyVisualTransformation(),
                        isError = !viewModel.isAmountValid,
                        supportingText = {
                            if (!viewModel.isAmountValid) {
                                Text(
                                    text = stringResource(R.string.amount_required),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.AttachMoney, contentDescription = null
                            )
                        })

                    OutlinedTextField(
                        value = viewModel.dateString,
                        onValueChange = { viewModel.onDateStringChange(it) },
                        label = { Text(stringResource(R.string.date)) },
                        modifier = Modifier.fillMaxWidth(),
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
                                    imageVector = Icons.Rounded.EditCalendar,
                                    contentDescription = stringResource(R.string.select_date)
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.CalendarToday, contentDescription = null
                            )
                        })

                    Text(
                        text = "Tipo",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EntryType.entries.forEachIndexed { index, entryType ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index, count = EntryType.entries.size
                                ),
                                onClick = { viewModel.onEntryTypeChange(entryType) },
                                selected = entryType == viewModel.entryType,
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = if (entryType == EntryType.INCOME) IncomeContainerColor else ExpenseContainerColor,
                                    activeContentColor = if (entryType == EntryType.INCOME) IncomeColor else ExpenseColor
                                )
                            ) {
                                Text(stringResource(entryType.label))
                            }
                        }
                    }
                }
            }

            if (viewModel.showDatePicker) {
                DatePickerModal(initialDate = viewModel.date, onDateSelected = { millis ->
                    millis?.let {
                        val selectedDate =
                            Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                        viewModel.onDateChange(selectedDate)
                    }
                }, onDismiss = { viewModel.onShowDatePickerChange(false) })
            }

            Button(
                onClick = { viewModel.saveEntry() },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Rounded.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.save), style = MaterialTheme.typography.titleMedium)
            }

            OutlinedButton(
                onClick = onNavigateToStatement,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.statement), style = MaterialTheme.typography.titleMedium
                )
            }
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
