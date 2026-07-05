package br.edu.utfpr.financeflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.edu.utfpr.financeflow.repository.EntryRepository

class StatementViewModelFactory(private val repository: EntryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}