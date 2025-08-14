package com.bloomtregua.rgb.viewmodels

import android.util.Log
import androidx.compose.ui.unit.min
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomtregua.rgb.database.budget.BudgetDao
import com.bloomtregua.rgb.database.budget.BudgetEntity
import com.bloomtregua.rgb.database.budget.BudgetResetType
import com.bloomtregua.rgb.database.categories.CategoryDao // Importa il tuo CategoryDao
import com.bloomtregua.rgb.database.categories.CategoryEntity // Importa la tua Entity Category
import com.bloomtregua.rgb.database.transactions.TransactionDao
import com.bloomtregua.rgb.dipendenceinjection.CategoryRepository
import com.bloomtregua.rgb.dipendenceinjection.DatabaseModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // StateFlow per esporre la lista di categorie alla UI in modo osservabile
    private val _categoriesUiModel = MutableStateFlow<List<CategoriaUiModel>>(emptyList())
    val categoriesUiModel: StateFlow<List<CategoriaUiModel>> = _categoriesUiModel.asStateFlow()

    // StateFlow per esporre lo stato di caricamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            categoryRepository.getCategorySolaUscita() // Ritorna Flow<List<CategoryEntity>>
                .map { categoryEntities ->
                    if (categoryEntities.isEmpty()) {
                        return@map emptyList<CategoriaUiModel>() // Mappa a lista UI vuota
                    }
                    categoryEntities.map { entity ->
                        Log.d("ViewModel", "categoria trovata: ${entity.categoryId} ${entity.categoryName}")
                        val spesa = recuperaSpesaPerCategoria(entity.categoryId, LocalDate.now()).toDouble()
                        entity.toCategoriaUiModel(spesa) // Adatta alla tua firma
                    }
                }
                .catch { e ->
                    Log.e("ViewModel", "Errore nel Flow delle categorie: ${e.message}", e)
                    _categoriesUiModel.value = emptyList() // Imposta lista vuota in caso di errore
                    _isLoading.value = false // Aggiorna isLoading anche in caso di errore
                }
                .collect { uiModels ->
                    _categoriesUiModel.value = uiModels
                    _isLoading.value = false // Imposta isLoading a false dopo che i dati sono stati raccolti e impostati
                }
        }
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
                val resetCategoryId = budget.budgetResetCategory
                if (resetCategoryId == null) {
                    Log.w("ViewModel", "budgetResetType è CATEGORY ma budgetResetCategory è null per budget ${budget.budgetId}")
                    return 0.0
                }

                val lastTransactionDateForResetCategory =
                    categoryRepository.getLastTransactionDateByCategoryId(resetCategoryId)

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
            else -> {
                Log.e("ViewModel", "Tipo di reset budget non riconosciuto: ${budget.budgetResetType} per budget ${budget.budgetId}")
                0.0 // Tipo di reset non riconosciuto, nessuna spesa calcolata
            }
        }
        return (calculatedSum ?: 0.0) // Se la somma è NULL (nessuna transazione), considerala 0.0
    }
}

