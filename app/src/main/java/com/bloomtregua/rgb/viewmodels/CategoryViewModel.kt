package com.bloomtregua.rgb.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomtregua.rgb.database.categories.CategoryDao // Importa il tuo CategoryDao
import com.bloomtregua.rgb.database.categories.CategoryEntity // Importa la tua Entity Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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


class CategoriesViewModel(private val categoryDao: CategoryDao) : ViewModel() {

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
            categoryDao.getCategorySolaUscita() // Assumendo ritorni Flow<List<CategoryEntity>>
                .map { categoryEntities ->
                    Log.d("ViewModel", "DAO ha emesso ${categoryEntities.size} entità.")
                    // Se categoryEntities è vuota qui, il problema è a monte (DAO/DB)
                    if (categoryEntities.isEmpty()) {
                        return@map emptyList<CategoriaUiModel>() // Mappa a lista UI vuota
                    }
                    categoryEntities.map { entity ->
                        val spesa = recuperaSpesaPerCategoria(entity.categoryId).toDouble()
                        entity.toCategoriaUiModel(spesa) // Adatta alla tua firma
                    }
                }
                .catch { e ->
                    Log.e("ViewModel", "Errore nel Flow delle categorie: ${e.message}", e)
                    _categoriesUiModel.value = emptyList() // Imposta lista vuota in caso di errore
                    _isLoading.value = false // Aggiorna isLoading anche in caso di errore
                    // Potresti voler emettere un evento di errore specifico per la UI qui
                }
                .collect { uiModels ->
                    Log.d("ViewModel", "Collezionati ${uiModels.size} modelli UI.")
                    _categoriesUiModel.value = uiModels
                    _isLoading.value = false // Imposta isLoading a false dopo che i dati sono stati raccolti e impostati
                }
        }
    }

    // Funzione fittizia di esempio: in realtà dovresti fare una query al TransactionDao
    private fun recuperaSpesaPerCategoria(categoryId: Long): Float {
        // Esempio: return transactionDao.getSpesaTotalePerCategoria(categoryId)
        return (50..200).random().toFloat() // Valore casuale per l'esempio
    }

}

