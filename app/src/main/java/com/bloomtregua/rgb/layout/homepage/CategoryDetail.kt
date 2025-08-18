package com.bloomtregua.rgb.layout.homepage

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bloomtregua.rgb.ui.theme.MarginXS
import com.bloomtregua.rgb.ui.theme.RGBTheme
import com.bloomtregua.rgb.viewmodels.CategoriaUiModel
import java.text.DecimalFormat

data class CategoriaUpdateData(
    val categoryId: Long,
    val newName: String?, // Permetti null se il nome non è stato modificato
    val newAllocatedAmount: Double? // Permetti null se l'importo non è stato modificato
    // Aggiungi altri campi che l'utente può modificare
)

@Composable
fun CategoryHeaderSection(
    category: CategoriaUiModel,
    currencyFormatter: DecimalFormat,
    onNavigateBack: () -> Unit,
    hasAlertCategoria : Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Categoria: ${category.categoryName}",
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        CategoriaDettaglio(
            categoria = category,
            modifier = Modifier.fillMaxWidth(),
            currencyFormatter = currencyFormatter,
            hasAlertCategoria = hasAlertCategoria,
            onCategoryClick = { }
        )
    }
}

@Composable
fun SubCategoryListItem(
    subCategory: CategoriaUiModel,
    currencyFormatter: DecimalFormat,
    onSubCategoryClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    CategoriaDettaglio(
        categoria = subCategory,
        modifier = Modifier.fillMaxWidth(),
        currencyFormatter = currencyFormatter,
        hasAlertCategoria = false,
        onCategoryClick = { }
    )
}

@Composable
fun CategoryEditableFieldsSection(
    category: CategoriaUiModel,
    onUpdateCategoryData: (CategoriaUpdateData) -> Unit,
    currencyFormatter: DecimalFormat, // Potrebbe servire per formattare/parseare input numerici
    modifier: Modifier = Modifier
) {
    var name by remember(category.categoryId, category.categoryName) { mutableStateOf(category.categoryName) }
    var allocatedAmountString by remember(category.categoryId, category.categoryAllAmount) {
        mutableStateOf(category.categoryAllAmount?.let { amount ->
            // Formatta inizialmente senza simbolo di valuta per la modifica
            val symbols = currencyFormatter.decimalFormatSymbols.clone() as java.text.DecimalFormatSymbols
            symbols.currencySymbol = "" // Rimuovi il simbolo per il campo di testo
            val tempFormatter = java.text.DecimalFormat(currencyFormatter.toPattern(), symbols)
            tempFormatter.format(amount).trim()
        } ?: "")
    }

    Column(modifier = modifier.padding(bottom = MarginXS)) {
        Text(
            "Modifica Dati Categoria:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome Categoria") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = allocatedAmountString,
            onValueChange = { allocatedAmountString = it },
            label = { Text("Budget Allocato") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        // Aggiungi altri campi qui

        Button(
            onClick = {
                val newAmount = try {
                    // Prova a fare il parse usando il formatter per gestire i separatori locali
                    currencyFormatter.parse(allocatedAmountString)?.toDouble()
                } catch (e: Exception) {
                    Log.e("EditableFields", "Errore nel parsing dell'importo: $allocatedAmountString", e)
                    null // O gestisci l'errore mostrando un messaggio all'utente
                }

                onUpdateCategoryData(
                    CategoriaUpdateData(
                        categoryId = category.categoryId,
                        newName = name,
                        newAllocatedAmount = newAmount
                    )
                )
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.End)
        ) {
            Text("Salva Modifiche")
        }
    }
}

@Preview(showBackground = true, name = "CategoryHeaderSection Preview - Categoria Principale")
@Composable
fun CategoryHeaderSectionPreviewCategoriaPrincipale1() {
    RGBTheme(dynamicColor = false) {
        Surface() {
            CategoryHeaderSection(
                category = CategoriaUiModel(
                    categoryId = 1L,
                    categoryName = "Shopping",
                    categoryAllAmount = 500.0,
                    totaleSpeso = 250.0,
                    totaleResiduo = 250.0,
                    percentualeSpeso = 50.0,
                    categoryMacroCategoryId = null
                ),
                currencyFormatter = DecimalFormat("#,##0.00"),
                hasAlertCategoria = true,
                onNavigateBack = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "CategoryHeaderSection Preview - Categoria Principale")
@Composable
fun CategoryHeaderSectionPreviewCategoriaPrincipale2() {
    RGBTheme(dynamicColor = false) {
        Surface() {
            CategoryHeaderSection(
                category = CategoriaUiModel(
                    categoryId = 1L,
                    categoryName = "Extra",
                    categoryAllAmount = 500.0,
                    totaleSpeso = 750.0,
                    totaleResiduo = -250.0,
                    percentualeSpeso = 50.0,
                    categoryMacroCategoryId = null
                ),
                currencyFormatter = DecimalFormat("#,##0.00"),
                hasAlertCategoria = false,
                onNavigateBack = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "CategoryEditableFieldsSection Preview - Con Dati")
@Composable
fun CategoryEditableFieldsSectionPreviewConDati() {
    RGBTheme(dynamicColor = false) {
        Surface() {
            CategoryEditableFieldsSection(
                category = CategoriaUiModel(
                    categoryId = 4L,
                    categoryName = "Utenze",
                    categoryAllAmount = 200.0,
                    totaleSpeso = 50.0,
                    totaleResiduo = 150.0,
                    percentualeSpeso = 25.0,
                    categoryMacroCategoryId = null
                ),
                onUpdateCategoryData = {},
                currencyFormatter = DecimalFormat("#,##0.00")
            )
        }
    }
}

@Preview(showBackground = true, name = "SubCategoryListItem Preview - Con Budget")
@Composable
fun SubCategoryListItemPreviewConBudget() {
    RGBTheme(dynamicColor = false) {
        Surface() {
            SubCategoryListItem(
                subCategory = CategoriaUiModel(
                    categoryId = 2L,
                    subcategoryId = 1L,
                    categoryName = "Alimentari",
                    categoryAllAmount = 300.0,
                    totaleSpeso = 120.50,
                    totaleResiduo = 179.50,
                    percentualeSpeso = 40.16,
                    categoryMacroCategoryId = 1L
                ),
                currencyFormatter = DecimalFormat("#,##0.00"),
                onSubCategoryClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "SubCategoryListItem Preview - Overbudget")
@Composable
fun SubCategoryListItemPreviewOverbudget() {
    RGBTheme(dynamicColor = false) {
        Surface() {
            SubCategoryListItem(
                subCategory = CategoriaUiModel(
                    categoryId = 3L,
                    subcategoryId = 1L,
                    categoryName = "Trasporti",
                    categoryAllAmount = 100.0,
                    totaleSpeso = 150.0, // Speso più del budget
                    totaleResiduo = -50.0,
                    percentualeSpeso = 150.0,
                    categoryMacroCategoryId = 1L
                ),
                currencyFormatter = DecimalFormat("#,##0.00"),
                onSubCategoryClick = {}
            )
        }
    }
}