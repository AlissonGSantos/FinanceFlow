package br.edu.utfpr.financeflow.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.financeflow.model.Entry
import br.edu.utfpr.financeflow.repository.EntryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class StatementUiEvent {
    data object EntryDeleted : StatementUiEvent()
}

class StatementViewModel(private val repository: EntryRepository) : ViewModel() {

    var entries by mutableStateOf<List<Entry>>(emptyList())
        private set

    var balance by mutableStateOf(0.0)
        private set

    var isLoading by mutableStateOf(true)
        private set

    private val _uiEvent = MutableSharedFlow<StatementUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            isLoading = true
            entries = repository.getAllEntries()
            balance = repository.getBalance()
            isLoading = false
        }
    }

    fun deleteEntry(entryId: Int) {
        viewModelScope.launch {
            repository.deleteEntry(entryId)
            loadData()
            _uiEvent.emit(StatementUiEvent.EntryDeleted)
        }
    }
}