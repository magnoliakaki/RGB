package com.bloomtregua.rgb.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomtregua.rgb.database.transactions.TransactionEntity
import com.bloomtregua.rgb.database.transactions.TransactionWithCategoryName
import com.bloomtregua.rgb.di.CategoryRepository
import com.bloomtregua.rgb.di.TransactionRepository
import com.bloomtregua.rgb.di.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

// Data class per rappresentare una transazione futura nella UI
data class ProssimaTransazioneUiModel(
    val id: Long,
    val descrizione: String,
    val importo: Double,
    val data: LocalDate,
    val nomeCategoria: String? = null
)

fun TransactionWithCategoryName.toProssimaTransazioneUiModel(): ProssimaTransazioneUiModel {
    return ProssimaTransazioneUiModel(
        id = this.transaction.transactionId,
        descrizione = this.transaction.transactionDescription ?: "Transazione",
        importo = this.transaction.transactionAmount * this.transaction.transactionSign, // Applica il segno per avere l'importo effettivo
        data = this.transaction.transactionDate,
        nomeCategoria = this.categoryName
    )
}

@HiltViewModel
class TransactionsViewModel@Inject constructor(
    private val transactionRepository: TransactionRepository,
    val userPreferencesRepository: UserPreferencesRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel(){

    val preferredLocale: StateFlow<java.util.Locale?> = userPreferencesRepository.userLocaleFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = java.util.Locale.getDefault()
        )

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

    private val _isZoomedIn = MutableStateFlow(false) // Inizialmente non zoomato (quindi solo il trafilo i basso con poche transazioni nella homepage)
    val isZoomedIn: StateFlow<Boolean> = _isZoomedIn.asStateFlow()


    fun setZoomIn(zoomed: Boolean) {
        _isZoomedIn.value = zoomed
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val prossimeTransazioni: StateFlow<List<ProssimaTransazioneUiModel>> =
        userPreferencesRepository.activeAccountIdFlow
            .flatMapLatest { accountId -> // Quando accountId cambia, questo blocco viene rieseguito
                if (accountId == null) {
                    // Se accountId è null, emetti una lista vuota di ProssimaTransazioneUiModel
                    flowOf(emptyList<ProssimaTransazioneUiModel>())
                } else {
                    // Se accountId non è null, combina con lo stato di isZoomedIn
                    isZoomedIn.flatMapLatest { zoomed ->
                        val limit =
                            if (zoomed) 10000 else 5

                        // Assicurati che getFutureTransactionsWithCategoryName accetti accountId e limit
                        transactionRepository.getFutureTransactionsWithCategoryName(limit, accountId)
                            .map { entities -> // entities è List<TransactionWithCategoryName>
                                entities.map { entity ->
                                    entity.toProssimaTransazioneUiModel()
                                }
                            }
                    }
                }
            }
            .catch { e ->
                Log.e("TransactionsVM", "Error collecting prossimeTransazioni", e)
                emit(emptyList<ProssimaTransazioneUiModel>())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList() // Valore iniziale mentre i flussi si inizializzano
            )


}