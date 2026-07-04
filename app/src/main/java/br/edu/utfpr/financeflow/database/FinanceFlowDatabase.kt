package br.edu.utfpr.financeflow.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.edu.utfpr.financeflow.model.Entry
import br.edu.utfpr.financeflow.model.EntryDao

@Database(
    entities = [Entry::class],
    version = 1
)
abstract class FinanceFlowDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
}
