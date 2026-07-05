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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
class HomeViewModel(private val repository: EntryRepository) : ViewModel() {
    private val dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy", Locale.getDefault())
    var description by mutableStateOf("")
        private set
    var amount by mutableStateOf("")
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
    }

    fun onAmountChange(newAmount: String) {
        amount = newAmount.filter { it.isDigit() }
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
        if (description.isBlank() || amount.isBlank() || !isDateValid) return

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
        }
    }
}