package br.edu.utfpr.financeflow.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.financeflow.model.Entry
import br.edu.utfpr.financeflow.model.EntryType
import br.edu.utfpr.financeflow.repository.EntryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class HomeViewModel(private val repository: EntryRepository) : ViewModel() {
    private val dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy", Locale.getDefault())

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    var description by mutableStateOf("")
        private set
    var isDescriptionValid by mutableStateOf(true)
        private set

    var amount by mutableStateOf("")
        private set
    var isAmountValid by mutableStateOf(true)
        private set

    var date: LocalDate by mutableStateOf(LocalDate.now())
        private set
    var dateString: String by mutableStateOf(date.format(dateFormatter))
        private set
    var showDatePicker by mutableStateOf(false)
        private set
    var isDateValid by mutableStateOf(true)
        private set

    var entryType: EntryType by mutableStateOf(EntryType.INCOME)
        private set


    fun onDescriptionChange(newDescription: String) {
        description = newDescription
        isDescriptionValid = true
    }

    fun onAmountChange(newAmount: String) {
        amount = newAmount.filter { it.isDigit() }
        isAmountValid = true
    }

    fun onDateChange(newDate: LocalDate) {
        date = newDate
        dateString = newDate.format(dateFormatter)
        isDateValid = true
    }

    fun onShowDatePickerChange(show: Boolean) {
        showDatePicker = show
    }

    fun onEntryTypeChange(newEntryType: EntryType) {
        entryType = newEntryType
    }

    fun onDateStringChange(newDateString: String) {
        val digitsOnly = newDateString.filter { it.isDigit() }
        if (digitsOnly.length <= 8) {
            dateString = digitsOnly
            if (digitsOnly.length == 8) {
                try {
                    date = LocalDate.parse(digitsOnly, dateFormatter)
                    isDateValid = true
                } catch (e: DateTimeParseException) {
                    Log.e("HomeViewModel", "Error parsing date: $digitsOnly", e)
                    isDateValid = false
                }
            } else {
                isDateValid = true
            }
        }
    }

    fun saveEntry() {
        isDescriptionValid = description.isNotBlank()
        isAmountValid = amount.isNotBlank()
        // If dateString length != 8 and not empty, it might be in middle of typing or invalid
        // But the current logic sets isDateValid in onDateStringChange.
        // Let's ensure isDateValid is also checked if it's incomplete.
        if (dateString.length != 8) {
            isDateValid = false
        }

        if (!isDescriptionValid || !isAmountValid || !isDateValid) return

        val entry = Entry(
            amount = amount.toDouble() / 100.0,
            description = description,
            date = date,
            type = entryType
        )

        viewModelScope.launch {
            repository.insertEntry(entry)
            description = ""
            amount = ""
            // Reset date to today or keep it? Usually better to keep it or reset it.
            // Requirement says "clear fields", so let's reset description and amount as done before.
            _uiEvent.emit(HomeUiEvent.EntrySaved)
        }
    }
}

sealed class HomeUiEvent {
    data object EntrySaved : HomeUiEvent()
}