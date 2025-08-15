package com.bloomtregua.rgb.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomtregua.rgb.database.budget.BudgetEntity
import com.bloomtregua.rgb.database.budget.BudgetResetType
import com.bloomtregua.rgb.database.categories.CategoryEntity
import com.bloomtregua.rgb.di.CategoryRepository
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
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.min

// Data class per rappresentare i dati della UI per una categoria
// (potresti averla già definita o adattarla dalla tua entity)
data class CategoriaUiModel(
    val categoryId: Long,
    val categoryName: String,
    val categoryAllAmount: Double?,   // Totale allocato della categoria
    val totaleSpeso: Double,         // Somma delle transazioni di questa categoria
    val totaleResiduo: Double,       // categoryAllAmount - totaleSpeso
    val percentualeSpeso: Double,    // Percentuale spesa sul totale categoria
    val categoryMacroCategoryId: Long?
)

fun CategoryEntity.toCategoriaUiModel(speso: Double): CategoriaUiModel {
    val budget = (this.categoryAllAmount ?: 0.0).toDouble()

    var percentuale = 0.0
    if (budget == 0.0 && speso > 0) {
        percentuale = 1.1
    } else {
        percentuale = (speso / budget)
    }
    return CategoriaUiModel(
        categoryId = this.categoryId,
        categoryName = this.categoryName,
        categoryAllAmount = budget,
        totaleSpeso = speso, // Valore da calcolare/recuperare
        totaleResiduo = budget - speso, // Calcolato
        percentualeSpeso = percentuale,
        categoryMacroCategoryId = this.categoryMacroCategoryId
    )
}


@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    userPreferencesRepository: UserPreferencesRepository // Iniettato
) : ViewModel() {

    private val _dataRefreshTrigger = MutableStateFlow(0L) // Trigger

    // StateFlow per esporre lo stato di caricamento
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoriesUiModel: StateFlow<List<CategoriaUiModel>> =
        userPreferencesRepository.activeAccountIdFlow
            .combine(_dataRefreshTrigger) { accountId, trigger ->
                Log.d("CategoriesViewModel", "Combine eseguito. Conto: $accountId, Trigger: $trigger")
                accountId
            }
            .flatMapLatest { accountId -> // 3. Quando conto o trigger cambiano, ricarica/ricalcola
                if (accountId == null) {
                    Log.w("CategoriesViewModel", "Nessun conto attivo selezionato.")
                    _isLoading.value = false
                    flowOf(emptyList<CategoriaUiModel>())
                } else {
                    Log.d("CategoriesViewModel", "flatMapLatest - Conto attivo: $accountId. Caricamento categorie e ricalcolo spese.")
                    categoryRepository.getCategorySolaUscitaByAccount(accountId) // Prendi le categorie per quel conto
                        .mapLatest { categoryEntities -> // Usa mapLatest se recuperaSpesa è suspend e vuoi cancellazione
                            val uiModels = if (categoryEntities.isEmpty()) {
                                Log.d("CategoriesViewModel", "Nessuna categoria per conto $accountId.")
                                emptyList<CategoriaUiModel>()
                            } else {
                                Log.d("CategoriesViewModel", "Mappatura di ${categoryEntities.size} categorie per conto $accountId.")
                                categoryEntities.map { entity ->
                                    Log.d("CategoriesViewModel", "Mappatura spesa per ${entity.categoryName} per conto $accountId.")
                                    val spesa = recuperaSpesaPerCategoria(entity.categoryId, LocalDate.now())
                                    entity.toCategoriaUiModel(spesa)
                                }
                            }
                            Log.d("CategoriesViewModel", "Mapping completato (conto $accountId). N. modelli UI: ${uiModels.size}. Imposto isLoading = false.")
                            _isLoading.value = false // << IMPOSTA isLoading A FALSE QUI, DOPO LA PRIMA ELABORAZIONE
                            uiModels // Restituisci i dati mappati
                        }
                    .onStart {
                        Log.d("CategoriesViewModel", "Flow interno (conto $accountId) ONSTART. Imposto isLoading = true.")
                        _isLoading.value = true
                    }
                    .onCompletion { throwable ->
                        if (throwable != null) {
                            Log.e("CategoriesViewModel", "Flow interno (conto $accountId) ONCOMPLETION con errore: ${throwable.message}")
                        } else {
                            Log.d("CategoriesViewModel", "Flow interno (conto $accountId) ONCOMPLETION normale (potrebbe non accadere per Flow di Room attivi).")
                        }
                        _isLoading.value = false
                    }
                    .catch { e ->
                        Log.e("CategoriesViewModel", "Flow interno (conto $accountId) CATCH: ${e.message}. Imposto isLoading = false.")
                        _isLoading.value = false // Errore, quindi finisci il caricamento
                        emit(emptyList<CategoriaUiModel>())
                    }

                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun notifyDataChanged() { // Occorre chiamare questo metodo quando budget/transazioni sono cambiate, per aggiornare i dati in homepage
        _dataRefreshTrigger.value = System.currentTimeMillis()
    }

    // Funzione aggiornata per recuperare la spesa, restituisce Double, gestendo null come 0.0
    private suspend fun recuperaSpesaPerCategoria(
        targetCategoryId: Long,
        currentDate: LocalDate
    ): Double {

        val budget: BudgetEntity? = categoryRepository.getBudgetSettings()
        if (budget == null) {
            Log.w("ViewModel", "Nessun budget trovato per categoryId: $targetCategoryId. Ritorno spesa 0.")
            return 0.0 // Nessun budget, quindi nessuna spesa calcolata secondo le regole del budget
        }

        val calculatedSum = when (budget.budgetResetType) {
            BudgetResetType.CATEGORY -> {
                if (budget.budgetResetCategory == null && budget.budgetResetSubCategory == null) {
                    Log.w("ViewModel", "budgetResetType è CATEGORY ma budgetResetCategory e budgetResetSubCategory sono entrambi null per budget ${budget.budgetId}")
                    return 0.0
                }

                val categoryId : Long = budget.budgetResetCategory ?: 0L
                val subCategoryId : Long = budget.budgetResetSubCategory ?: 0L

                val lastTransactionDateForResetCategory: LocalDate? = if (budget.budgetResetSubCategory == null) {
                    categoryRepository.getLastTransactionDateByCategoryId(categoryId)
                } else {
                    categoryRepository.getLastTransactionDateBySubCategoryId(subCategoryId)
                }

                val startDate = if (lastTransactionDateForResetCategory == null) {
                    categoryRepository.getMinTransactionDateByCategoryId(targetCategoryId)  // Se è null prendo tutte le transazioni della mia categoria
                } else {
                    lastTransactionDateForResetCategory.plusDays(1)
                }

                if (startDate == null) {
                    return 0.0
                } else {
                    categoryRepository.getSumTransactionsFromDateByCategoryId(targetCategoryId, startDate)
                }
            }
            BudgetResetType.DATE -> {
                val resetDay = budget.budgetResetDay
                if (resetDay == null) {
                    Log.w("ViewModel", "budgetResetType è DATE ma budgetResetDay è null per budget ${budget.budgetId}")
                    return 0.0
                }

                val calculatedStartDate: LocalDate
                val currentDayOfMonth = currentDate.dayOfMonth
                val currentMonthValue = currentDate.monthValue // Usa monthValue per intero 1-12
                val currentYear = currentDate.year

                // Valida resetDay
                if (resetDay < 1 || resetDay > 31) {
                    Log.e("ViewModel", "budgetResetDay ($resetDay) non valido per budget ${budget.budgetId}")
                    return 0.0
                }

                if (currentDayOfMonth >= resetDay) {
                    val dayForCurrentMonth = min(resetDay, currentDate.month.length(currentDate.isLeapYear))
                    calculatedStartDate = LocalDate.of(currentYear, currentMonthValue, dayForCurrentMonth)
                } else {
                    // La startDate è il resetDay del mese precedente.
                    val previousMonthDate = currentDate.minusMonths(1)
                    // Assicurati che il giorno sia valido per il mese precedente
                    val dayForPreviousMonth = min(resetDay, previousMonthDate.month.length(previousMonthDate.isLeapYear))
                    calculatedStartDate = LocalDate.of(previousMonthDate.year, previousMonthDate.monthValue, dayForPreviousMonth)
                }
                Log.d("ViewModel", "Budget DATE reset per category $targetCategoryId. StartDate calcolata: $calculatedStartDate")
                categoryRepository.getSumTransactionsFromDateByCategoryId(targetCategoryId, calculatedStartDate)
            }
        }
        return (calculatedSum ?: 0.0) * -1 // Se la somma è NULL (nessuna transazione), considerala 0.0. In ogni caso, moltiplico * -1 per avere le detrazoni in positivo e avere l'avanzamento della barra, e viceversa per le entrate
    }
}

