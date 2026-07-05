package br.edu.utfpr.financeflow.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.time.LocalDate

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val description: String,
    val date: LocalDate,
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

    @Query("SELECT \n" +
            "    SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) -\n" +
            "    SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) AS net_balance\n" +
            "FROM entries;")
    fun getBalance(): Double
}