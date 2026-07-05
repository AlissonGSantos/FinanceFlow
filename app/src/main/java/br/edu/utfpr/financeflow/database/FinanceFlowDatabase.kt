package br.edu.utfpr.financeflow.database

import br.edu.utfpr.financeflow.utils.Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.edu.utfpr.financeflow.model.Entry
import br.edu.utfpr.financeflow.model.EntryDao

@Database(
    entities = [Entry::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class FinanceFlowDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
}
