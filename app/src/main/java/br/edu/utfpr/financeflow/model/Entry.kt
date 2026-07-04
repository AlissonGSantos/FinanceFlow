package br.edu.utfpr.financeflow.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.sql.Date

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val description: String,
    val date: Date,
    val type: EntryType
)

enum class EntryType {
    INCOME,
    EXPENSE
}

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries ORDER BY date DESC")
    fun getAllEntries(): List<Entry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntry(entry: Entry)

    @Query("DELETE FROM entries WHERE id = :entryId")
    fun deleteEntry(entryId: Int)
}