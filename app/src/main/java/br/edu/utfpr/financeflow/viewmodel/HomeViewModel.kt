package br.edu.utfpr.financeflow.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {
    private val dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy")

    var description by mutableStateOf("")
        private set
    var amount by mutableStateOf("")
        private set
    var date: LocalDate by mutableStateOf(LocalDate.now())
        private set
    var dateString: String by mutableStateOf(date.format(dateFormatter))
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
    }

    fun onDateStringChange(newDateString: String) {
        val digitsOnly = newDateString.filter { it.isDigit() }
        if (digitsOnly.length <= 8) {
            dateString = digitsOnly
            if (digitsOnly.length == 8) {
                try {
                    date = LocalDate.parse(digitsOnly, dateFormatter)
                } catch (e: Exception) {
                    // Data inválida, pode-se tratar o erro aqui futuramente
                }
            }
        }
    }

    fun saveEntry() {
        // Lógica para salvar a entrada no banco de dados
    }
}