package br.edu.utfpr.financeflow

import android.app.Application
import androidx.room.Room
import br.edu.utfpr.financeflow.database.FinanceFlowDatabase
import br.edu.utfpr.financeflow.repository.EntryRepository

class FinanceFlowApplication : Application() {
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            FinanceFlowDatabase::class.java,
            "finance_flow_database"
        ).build()
    }

    val repository by lazy {
        EntryRepository(database.entryDao())
    }
}