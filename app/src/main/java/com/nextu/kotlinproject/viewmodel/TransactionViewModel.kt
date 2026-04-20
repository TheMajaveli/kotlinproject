package com.nextu.kotlinproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nextu.kotlinproject.data.model.Transaction
import com.nextu.kotlinproject.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(private val repo: TransactionRepository) : ViewModel() {

    val transactions: Flow<List<Transaction>> = repo.transactions

    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction.asStateFlow()

    fun add(t: Transaction) = viewModelScope.launch {
        repo.insert(t)
    }

    fun update(t: Transaction) = viewModelScope.launch {
        repo.update(t)
    }

    fun delete(t: Transaction) = viewModelScope.launch {
        repo.delete(t)
    }

    fun loadById(id: Int) = viewModelScope.launch {
        _selectedTransaction.value = repo.getById(id)
    }

    fun clearSelection() {
        _selectedTransaction.value = null
    }
}

class TransactionViewModelFactory(
    private val repo: TransactionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}