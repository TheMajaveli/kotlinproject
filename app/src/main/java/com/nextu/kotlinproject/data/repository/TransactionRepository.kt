package com.nextu.kotlinproject.data.repository

import com.nextu.kotlinproject.data.local.TransactionDao
import com.nextu.kotlinproject.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {

    val transactions: Flow<List<Transaction>> = dao.getAll()

    suspend fun insert(transaction: Transaction) {
        dao.insert(transaction)
    }

    suspend fun update(transaction: Transaction) {
        dao.update(transaction)
    }

    suspend fun delete(transaction: Transaction) {
        dao.delete(transaction)
    }

    suspend fun getById(id: Int): Transaction {
        return dao.getById(id)
            ?: throw IllegalStateException("Transaction $id not found")
    }

}