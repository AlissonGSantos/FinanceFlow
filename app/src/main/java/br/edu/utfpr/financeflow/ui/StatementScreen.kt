package br.edu.utfpr.financeflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.financeflow.FinanceFlowApplication
import br.edu.utfpr.financeflow.R
import br.edu.utfpr.financeflow.model.Entry
import br.edu.utfpr.financeflow.model.EntryType
import br.edu.utfpr.financeflow.ui.theme.ExpenseColor
import br.edu.utfpr.financeflow.ui.theme.ExpenseContainerColor
import br.edu.utfpr.financeflow.ui.theme.IncomeColor
import br.edu.utfpr.financeflow.ui.theme.IncomeContainerColor
import br.edu.utfpr.financeflow.viewmodel.StatementUiEvent
import br.edu.utfpr.financeflow.viewmodel.StatementViewModel
import br.edu.utfpr.financeflow.viewmodel.StatementViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatementScreen(
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as FinanceFlowApplication).repository
    val viewModel: StatementViewModel = viewModel(factory = StatementViewModelFactory(repository))
    val snackbarHostState = remember { SnackbarHostState() }
    val entryDeletedMessage = stringResource(R.string.entry_deleted_success)

    var entryToDelete by remember { mutableStateOf<Entry?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is StatementUiEvent.EntryDeleted -> {
                    snackbarHostState.showSnackbar(entryDeletedMessage)
                }
            }
        }
    }

    if (entryToDelete != null) {
        DeleteConfirmationDialog(onConfirm = {
            entryToDelete?.let { viewModel.deleteEntry(it.id) }
            entryToDelete = null
        }, onDismiss = {
            entryToDelete = null
        })
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    stringResource(R.string.statement), fontWeight = FontWeight.Bold
                )
            }, navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (viewModel.entries.isNotEmpty()) {
                    BalanceHeader(viewModel.balance)
                }

                if (viewModel.entries.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.no_entries),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.entries, key = { it.id }) { entry ->
                            EntryItemWrapper(
                                entry = entry, onDelete = { entryToDelete = it })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceHeader(balance: Double) {
    val currencyFormat = NumberFormat.getCurrencyInstance()
    val balanceColor = if (balance >= 0) IncomeColor else ExpenseColor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.total_balance),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = currencyFormat.format(balance),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = balanceColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryItemWrapper(
    entry: Entry, onDelete: (Entry) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled, confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete(entry)
            }
            false
        })

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DismissBackground(dismissState) },
        enableDismissFromStartToEnd = false
    ) {
        EntryItem(entry)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color, shape = MaterialTheme.shapes.medium)
            .padding(horizontal = 20.dp), contentAlignment = Alignment.CenterEnd
    ) {
        if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_entry_title)) },
        text = { Text(stringResource(R.string.delete_entry_confirmation)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        })
}

@Composable
fun EntryItem(entry: Entry) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
    val currencyFormat = NumberFormat.getCurrencyInstance()
    val isIncome = entry.type == EntryType.INCOME
    val color = if (isIncome) IncomeColor else ExpenseColor
    val containerColor = if (isIncome) IncomeContainerColor else ExpenseContainerColor
    val icon =
        if (isIncome) Icons.AutoMirrored.Rounded.TrendingUp else Icons.AutoMirrored.Rounded.TrendingDown
    val prefix = if (isIncome) "+" else "-"

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = containerColor.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = entry.date.format(dateFormatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "$prefix ${currencyFormat.format(entry.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
