package br.edu.utfpr.financeflow.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.financeflow.model.Entry
import br.edu.utfpr.financeflow.repository.EntryRepository
import kotlinx.coroutines.launch

class StatementViewModel(private val repository: EntryRepository) : ViewModel() {

    var entries by mutableStateOf<List<Entry>>(emptyList())
        private set

    var balance by mutableStateOf(0.0)
        private set

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            entries = repository.getAllEntries()
            balance = repository.getBalance()
        }
    }

    fun deleteEntry(entryId: Int) {
        viewModelScope.launch {
            repository.deleteEntry(entryId)
            loadData()
        }
    }
}