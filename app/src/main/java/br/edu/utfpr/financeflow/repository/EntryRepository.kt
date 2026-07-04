package br.edu.utfpr.financeflow.repository

import br.edu.utfpr.financeflow.model.Entry
import br.edu.utfpr.financeflow.model.EntryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EntryRepository(private val entryDao: EntryDao) {

    suspend fun getAllEntries(): List<Entry> = withContext(Dispatchers.IO) {
        entryDao.getAllEntries()
    }

    suspend fun insertEntry(entry: Entry) = withContext(Dispatchers.IO) {
        entryDao.insertEntry(entry)
    }

    suspend fun deleteEntry(entryId: Int) = withContext(Dispatchers.IO) {
        entryDao.deleteEntry(entryId)
    }

}