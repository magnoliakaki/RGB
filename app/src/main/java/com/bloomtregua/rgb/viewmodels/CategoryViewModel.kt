package com.bloomtregua.rgb.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomtregua.rgb.database.budget.BudgetEntity
import com.bloomtregua.rgb.database.budget.BudgetResetType
import com.bloomtregua.rgb.database.categories.CategoryEntity
import com.bloomtregua.rgb.database.categories.SubcategoryEntity
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
import kotlinx.coroutines.flow.firstOrNull
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
data class CategoriaUiModel(
    val categoryId: Long,
    val subcategoryId: Long? = null,
    val categoryName: String,
    val categoryAllAmount: Double?,   // Totale allocato della categoria
    val totaleSpeso: Double,         // Somma delle transazioni di questa categoria
    val totaleResiduo: Double,       // categoryAllAmount - totaleSpeso
    val percentualeSpeso: Double,    // Percentuale spesa sul totale categoria
    val categoryMacroCategoryId: Long?,
    val hasErrorInSubcategories: Boolean = false
)

fun CategoryEntity.toCategoriaUiModel(speso: Double, hasError: Boolean): CategoriaUiModel {
    val budget = (this.categoryAllAmount ?: 0.0).toDouble()

    val percentuale: Double = if (budget == 0.0 && speso > 0) {
        1.1
    } else {
        (speso / budget)
    }
    return CategoriaUiModel(
        categoryId = this.categoryId,
        subcategoryId = null,
        categoryName = this.categoryName,
        categoryAllAmount = budget,
        totaleSpeso = speso, // Valore da calcolare/recuperare
        totaleResiduo = budget - speso, // Calcolato
        percentualeSpeso = percentuale,
        categoryMacroCategoryId = this.categoryMacroCategoryId,
        hasErrorInSubcategories = hasError
    )
}

fun SubcategoryEntity.toCategoriaUiModel(speso: Double, categoryMacroCategoryId: Long?): CategoriaUiModel {
    val budget = (this.subcategoryAllAmount ?: 0.0).toDouble()

    val percentuale: Double = if (budget == 0.0 && speso > 0) {
        1.1
    } else {
        (speso / budget)
    }
    return CategoriaUiModel(
        categoryId = this.subcategoryCategoryId,
        subcategoryId = this.subcategoryId,
        categoryName = this.subcategoryName,
        categoryAllAmount = budget,
        totaleSpeso = speso, // Valore da calcolare/recuperare
        totaleResiduo = budget - speso, // Calcolato
        percentualeSpeso = percentuale,
        categoryMacroCategoryId = categoryMacroCategoryId
    )
}

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    userPreferencesRepository: UserPreferencesRepository // Iniettato
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    // Funzione per selezionare una categoria
    fun selectCategory(categoryId: Long) {
        _selectedCategoryId.value = categoryId
    }

    // Funzione per tornare alla vista principale (deselezionare)
    fun clearSelectedCategory() {
        _selectedCategoryId.value = null
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedCategoryDetails: StateFlow<CategoriaUiModel?> =
        _selectedCategoryId.flatMapLatest { id ->
            if (id == null) {
                flowOf(null)
            } else {
                categoryRepository.getCategoryById(id) // Assumendo che questo restituisca Flow<CategoryEntity?>
                    .flatMapLatest { categoryEntity ->
                        if (categoryEntity == null) {
                            flowOf(null)
                        } else {
                            // Simile alla logica in categoriesUiModel per una singola categoria
                            flowOf(categoryEntity).mapLatest { entity ->
                                var spesa = recuperaSpesaPerCategoria(entity.categoryId, null, LocalDate.now())
                                val hasError = categoryRepository.getPresenzaAllertInAccountOrCategory(entity.categoryAccountId,entity.categoryId) > 0

                                if (spesa == -0.00) {
                                    spesa = 0.00
                                }
                                entity.toCategoriaUiModel(spesa, hasError)
                            }
                        }
                    }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val subCategories: StateFlow<List<CategoriaUiModel>> = _selectedCategoryId.flatMapLatest { id ->
        if (id == null) {
            flowOf(emptyList())
        } else {

            categoryRepository.getSubcategoryByCategoryId(id) // Assumendo Flow<List<CategoryEntity>>
                .mapLatest { subCategoryEntities ->
                    // Recupera la categoria padre una sola volta
                    val parentCategory = categoryRepository.getCategoryById(id)
                    val parentMacroCategoryId = parentCategory.firstOrNull()?.categoryMacroCategoryId

                    subCategoryEntities.map { entity ->
                        var spesa = recuperaSpesaPerCategoria(entity.subcategoryCategoryId,entity.subcategoryId, LocalDate.now())
                        if (spesa == -0.00) {
                            spesa = 0.00
                        }
                        // Passa il categoryMacroCategoryId della categoria padre
                        entity.toCategoriaUiModel(spesa, parentMacroCategoryId)
                    }
                }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _dataRefreshTrigger = MutableStateFlow(0L) // Trigger

    // StateFlow per esporre lo stato di caricamento
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun setLoadingValue(valore: Boolean) {
        _isLoading.value = valore
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoriesUiModel: StateFlow<List<CategoriaUiModel>> =
        userPreferencesRepository.activeAccountIdFlow
            .combine(_dataRefreshTrigger) { accountId, trigger ->
                accountId
            }
            .flatMapLatest { accountId -> // 3. Quando conto o trigger cambiano, ricarica/ricalcola
                if (accountId == null) {
                    _isLoading.value = false
                    flowOf(emptyList<CategoriaUiModel>())
                } else {
                    categoryRepository.getCategorySolaUscitaByAccount(accountId) // Prendi le categorie per quel conto
                        .mapLatest { categoryEntities -> // Usa mapLatest se recuperaSpesa è suspend e vuoi cancellazione
                            val uiModels = if (categoryEntities.isEmpty()) {
                                emptyList<CategoriaUiModel>()
                            } else {
                                categoryEntities.map { entity ->
                                    var spesa = recuperaSpesaPerCategoria(entity.categoryId, null, LocalDate.now())
                                    val hasError = categoryRepository.getPresenzaAllertInAccountOrCategory(entity.categoryAccountId,entity.categoryId) > 0

                                    // Per qualche motivo, nella funzione sopra 0*-1 fa -0...
                                    if (spesa == -0.00){
                                        spesa = 0.00
                                    }
                                    entity.toCategoriaUiModel(spesa, hasError)
                                }
                            }
                            _isLoading.value = false // << IMPOSTA isLoading A FALSE QUI, DOPO LA PRIMA ELABORAZIONE
                            uiModels // Restituisci i dati mappati
                        }
                    .onStart {
                        _isLoading.value = true
                    }
                    .onCompletion { throwable ->
                        _isLoading.value = false
                    }
                    .catch { e ->
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
        targetSubCategoryId: Long? = null,
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

                val lastTransactionTimestampForResetCategory = if (budget.budgetResetSubCategory == null) {
                    categoryRepository.getLastTransactionTimestampByCategoryId(categoryId)
                } else {
                    categoryRepository.getLastTransactionTimestampBySubCategoryId(subCategoryId)
                }

                val startDate = if (lastTransactionTimestampForResetCategory == null) {
                    categoryRepository.getMinTransactionTimestampByCategoryId(targetCategoryId)  // Se è null prendo tutte le transazioni della mia categoria
                } else {
                    lastTransactionTimestampForResetCategory.plus(1, java.time.temporal.ChronoUnit.SECONDS) // Prendo tutto quello inserito subito dopo la transazione di reset
                }

                if (startDate == null) {
                    return 0.0
                } else {

                    // Se metto la sottocategoria prendo SOLO i dati della sottocategoria, altrimenti prendo tutto dalla categoria
                    if(targetSubCategoryId != null){
                        categoryRepository.getSumTransactionsFromTimestampBySubcategoryId(targetSubCategoryId,startDate)
                    } else {
                        categoryRepository.getSumTransactionsFromTimestampByCategoryId(targetCategoryId,startDate)
                    }
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

                if (resetDay < 1 || resetDay > 31) {
                    Log.e("ViewModel", "budgetResetDay ($resetDay) non valido per budget ${budget.budgetId}")
                    return 0.0
                }

                if (currentDayOfMonth >= resetDay) {
                    val dayForCurrentMonth = min(resetDay, currentDate.month.length(currentDate.isLeapYear))
                    calculatedStartDate = LocalDate.of(currentYear, currentMonthValue, dayForCurrentMonth)
                } else {
                    val previousMonthDate = currentDate.minusMonths(1)
                    val dayForPreviousMonth = min(resetDay, previousMonthDate.month.length(previousMonthDate.isLeapYear))
                    calculatedStartDate = LocalDate.of(previousMonthDate.year, previousMonthDate.monthValue, dayForPreviousMonth)
                }

                // Se metto la sottocategoria prendo SOLO i dati della sottocategoria, altrimenti prendo tutto dalla categoria
                if(targetSubCategoryId != null){
                    categoryRepository.getSumTransactionsFromDateBySubcategoryId(targetSubCategoryId,calculatedStartDate)
                } else {
                    categoryRepository.getSumTransactionsFromDateByCategoryId(targetCategoryId,calculatedStartDate)
                }
            }
        }
        return (calculatedSum ?: 0.0) * -1 // Se la somma è NULL (nessuna transazione), considerala 0.0. In ogni caso, moltiplico * -1 per avere le detrazoni in positivo e avere l'avanzamento della barra, e viceversa per le entrate
    }
}

