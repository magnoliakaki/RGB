package com.bloomtregua.rgb.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomtregua.rgb.database.transactions.TransactionEntity
import com.bloomtregua.rgb.dipendenceinjection.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TransactionUiModel(
    val transactionId: Long,
    val transactionDescription: String?,
    val transactionDate: LocalDate,
    val transactionSign: Int,
    val transactionAmount: Double,
    val transactionCategoryId: Long?
)

fun TransactionEntity.toTransactionUiModel(): TransactionUiModel {
    return TransactionUiModel(
        transactionId = this.transactionId,
        transactionDescription = this.transactionDescription,
        transactionDate = this.transactionDate,
        transactionSign = this.transactionSign,
        transactionAmount = this.transactionAmount,
        transactionCategoryId = this.transactionCategoryId
    )
}

@HiltViewModel
class TransactionsViewModel@Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel(){
    // StateFlow per esporre la lista di transazioni alla UI in modo osservabile
    private val _transactionsUiModel = MutableStateFlow<List<TransactionUiModel>>(emptyList())
    val transactionsUiModel: StateFlow<List<TransactionUiModel>> =
        _transactionsUiModel.asStateFlow()

    // StateFlow per esporre lo stato di caricamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            transactionRepository.getAllTransactions()
                .map { transactionEntity ->
                    if (transactionEntity.isEmpty()) {
                        return@map emptyList<TransactionUiModel>() // Mappa a lista UI vuota
                    }
                    transactionEntity.map { entity ->
                        entity.toTransactionUiModel()
                    }
                }
                .catch { e ->
                    _transactionsUiModel.value = emptyList() // Imposta lista vuota in caso di errore
                    _isLoading.value = false // Aggiorna isLoading anche in caso di errore
                    // Potresti voler emettere un evento di errore specifico per la UI qui
                }
                .collect{ uiModels ->
                    _transactionsUiModel.value = uiModels
                    _isLoading.value = false // Imposta isLoading a false dopo che i dati sono stati raccolti e impostati
                }
        }
    }
}