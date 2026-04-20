package com.nextu.kotlinproject.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.nextu.kotlinproject.data.model.Profile
import com.nextu.kotlinproject.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Int): Transaction?
}

@Database(
    entities = [Transaction::class, Profile::class],
    version = 4
)
abstract class AppDb : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun profileDao(): ProfileDao
}