package com.bloomtregua.rgb.layout.homepage

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bloomtregua.rgb.ui.theme.MarginXS
import com.bloomtregua.rgb.ui.theme.RGBTheme
import com.bloomtregua.rgb.viewmodels.CategoriaUiModel
import com.bloomtregua.rgb.viewmodels.CategoriaUpdateData
import com.bloomtregua.rgb.viewmodels.SottocategoriaUpdateData
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

data class SubcategoriaUpdateData(
    val categoryId: Long,
    val newName: String?, // Permetti null se il nome non è stato modificato
    val newAllocatedAmount: Double? // Permetti null se l'importo non è stato modificato
    // Aggiungi altri campi che l'utente può modificare
)

@Composable
fun SubcategoryHeaderSection(
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
                text = "Sottocat. : ${category.categoryName}",
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        CategoriaDettaglio(
            categoria = category,
            modifier = Modifier.fillMaxWidth(),
            currencyFormatter = currencyFormatter,
            hasAlertCategoria = hasAlertCategoria,
            onCategoryClick = { },
            onSubCategoryClick = { },
        )
    }
}

@Composable
fun SubcategoryEditableFieldsSection(
    category: CategoriaUiModel,
    onUpdateSubcategoryData: (SottocategoriaUpdateData) -> Unit,
    currencyFormatter: DecimalFormat, // Potrebbe servire per formattare/parseare input numerici
    modifier: Modifier = Modifier
) {
    var SubcategoryName by remember(category.categoryId, category.categoryName) { mutableStateOf(category.categoryName) }
    var SubcategoryAllocatedAmountString by remember(category.categoryId, category.categoryAllAmount) {
        mutableStateOf(category.categoryAllAmount?.let { amount ->
            DecimalFormat("0.00").format(amount)
        } ?: "")
    }

    Column(modifier = modifier.padding(bottom = MarginXS)) {
        Text(
            "Modifica Dati Sottocategoria:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            "Budget Allocato:  " + currencyFormatter.format(category.categoryAllAmount),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        OutlinedTextField(
            value = SubcategoryName,
            onValueChange = { newName ->
                if (!newName.contains("\n")) {
                    SubcategoryName = newName
                }
            },
            label = { Text("Nome Sottocategoria") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true, // Ottimizza per una singola riga
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next // Cambia l'azione IME se seguito da altri campi
            )
        )
//        Spacer(modifier = Modifier.height(8.dp))
//        OutlinedTextField(
//            value = SubcategoryAllocatedAmountString,
//            onValueChange = { newValue ->
//                // Permetti solo numeri, un separatore decimale e fino a due cifre decimali
//                val regex = Regex("^\\d*([.,]?\\d{0,2})$")
//                if (newValue.isEmpty() || regex.matches(newValue)) {
//                    SubcategoryAllocatedAmountString = newValue.replace(',', '.') // Normalizza a punto per il parsing
//                }
//            },
//            label = { Text("Budget Allocato") },
//            modifier = Modifier.fillMaxWidth(),
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Number, // Tastiera numerica ottimizzata
//                imeAction = ImeAction.Done // Azione IME per l'ultimo campo
//            ),
//            readOnly = true,
//            singleLine = true, // Ottimizza per una singola riga
//            prefix = { Text("€ ") } // Mostra il simbolo dell'euro come prefisso
//        )
        // Aggiungi altri campi qui

        Button(
            onClick = {
//                val newAmount = if (SubcategoryAllocatedAmountString.isNotBlank()) {
//                    try {
//                        SubcategoryAllocatedAmountString.replace(',', '.').toDouble()
//                    } catch (e: NumberFormatException) {
//                        Log.e("EditableFields", "Errore nel parsing dell'importo: $SubcategoryAllocatedAmountString", e)
//                        0.00
//                    }
//                } else {
//                    0.00
//                }

                onUpdateSubcategoryData(
                    SottocategoriaUpdateData(
                        categoryId = category.categoryId,
                        subcategoryId = category.subcategoryId,
                        newName = SubcategoryName
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
fun SubcategoryHeaderSectionPreviewCategoriaPrincipale() {
    RGBTheme(dynamicColor = false) {
        Surface() {
            SubcategoryHeaderSection(
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
fun SubcategoryEditableFieldsSectionPreviewConDati() {
    RGBTheme(dynamicColor = false) {
        Surface() {
            SubcategoryEditableFieldsSection(
                category = CategoriaUiModel(
                    categoryId = 4L,
                    categoryName = "Utenze",
                    categoryAllAmount = 200.0,
                    totaleSpeso = 50.0,
                    totaleResiduo = 150.0,
                    percentualeSpeso = 25.0,
                    categoryMacroCategoryId = null
                ),
                onUpdateSubcategoryData = {},
                currencyFormatter = DecimalFormat("#,##0.00")
            )
        }
    }
}