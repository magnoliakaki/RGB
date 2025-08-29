package com.bloomtregua.rgb.layout.homepage

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bloomtregua.rgb.ui.theme.MarginXS
import com.bloomtregua.rgb.ui.theme.RGBTheme
import com.bloomtregua.rgb.viewmodels.CategoriaUiModel
import com.bloomtregua.rgb.viewmodels.CategoriaUpdateData
import com.bloomtregua.rgb.viewmodels.CategoriesViewModel.MacroCategoryUiModel
import java.text.DecimalFormat

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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro", modifier = Modifier.padding(end = 16.dp))
            }
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
            onCategoryClick = { },
            onSubCategoryClick = { },
        )
    }
}

@Composable
fun SubCategoryListItem(
    subCategory: CategoriaUiModel,
    currencyFormatter: DecimalFormat,
    onSubCategoryClick: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    CategoriaDettaglio(
        categoria = subCategory,
        modifier = modifier.fillMaxWidth(),
        currencyFormatter = currencyFormatter,
        hasAlertCategoria = false,
        onCategoryClick = { },
        onSubCategoryClick = { subcategoryId ->
            onSubCategoryClick(subcategoryId)
            Log.d("SubCategoryListItem", "Cliccato SubCategory: $subcategoryId")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditableFieldsSection(
    category: CategoriaUiModel,
    onUpdateCategoryData: (CategoriaUpdateData) -> Unit,
    availableMacroCategories: List<MacroCategoryUiModel>, // Lista passata dal ViewModel
    currencyFormatter: DecimalFormat, // Potrebbe servire per formattare/parseare input numerici
    modifier: Modifier = Modifier
) {
    var name by remember(category.categoryId, category.categoryName) { mutableStateOf(category.categoryName) }
    var allocatedAmountString by remember(category.categoryId, category.categoryAllAmount) {
        mutableStateOf(category.categoryAllAmount?.let { amount ->
            DecimalFormat("0.00").format(amount)
        } ?: "")
    }

    // --- SPINNER PER MACRO CATEGORIA ---
    var macroCategoryMenuExpanded by remember { mutableStateOf(false) }
    var userSelectedMacroInSpinner by remember(category.categoryMacroCategoryId, availableMacroCategories) {
        mutableStateOf(
            availableMacroCategories.find { it.macroCategoryId == category.categoryMacroCategoryId }
        )
    }

    Column(modifier = modifier.padding(bottom = MarginXS)) {
        Text(
            "Modifica Dati Categoria:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            "Budget Allocato:  " + currencyFormatter.format(category.categoryAllAmount),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        OutlinedTextField(
            value = name,
            onValueChange = { newName ->
                // Impedisci l'inserimento di newline
                if (!newName.contains("\n")) {
                    name = newName
                }
            },
            label = { Text("Nome Categoria") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true, // Ottimizza per una singola riga
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next // Cambia l'azione IME se seguito da altri campi
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
//        OutlinedTextField(
//            value = allocatedAmountString,
//            onValueChange = { newValue ->
//                // Permetti solo numeri, un separatore decimale e fino a due cifre decimali
//                val regex = Regex("^\\d*([.,]?\\d{0,2})$")
//                if (newValue.isEmpty() || regex.matches(newValue)) {
//                    allocatedAmountString = newValue.replace(',', '.') // Normalizza a punto per il parsing
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
//        Spacer(modifier = Modifier.height(8.dp))

        // --- SPINNER PER MACRO CATEGORIA ---
        ExposedDropdownMenuBox(
            expanded = macroCategoryMenuExpanded,
            onExpandedChange = { macroCategoryMenuExpanded = !macroCategoryMenuExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = userSelectedMacroInSpinner?.name ?: "Nessuna", // Mostra il nome della macro selezionata
                onValueChange = { /* Non modificabile direttamente, solo tramite selezione */ },
                label = { Text("Macro Categoria") },
                readOnly = true, // L'utente seleziona, non digita
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = macroCategoryMenuExpanded)
                },
                modifier = Modifier
                    .menuAnchor() // Importante per posizionare il menu
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = macroCategoryMenuExpanded,
                onDismissRequest = { macroCategoryMenuExpanded = false }
            ) {
                availableMacroCategories.forEach { macro ->
                    DropdownMenuItem(
                        text = { Text(macro.name) },
                        onClick = {
                            userSelectedMacroInSpinner = macro // Aggiorna lo stato LOCALE
                            macroCategoryMenuExpanded = false
                        }
                    )
                }
            }
        }
        // --- FINE SPINNER ---


        Button(
            onClick = {
//                val newAmount = if (allocatedAmountString.isNotBlank()) {
//                    try {
//                        allocatedAmountString.replace(',', '.').toDouble()
//                    } catch (e: NumberFormatException) {
//                        Log.e("EditableFields", "Errore nel parsing dell'importo: $allocatedAmountString", e)
//                        0.00
//                    }
//                } else {
//                    0.00
//                }

                // Prendi l'ID dalla macro categoria SELEZIONATA DALL'UTENTE (stato locale)
                val selectedMacroIdByUser = userSelectedMacroInSpinner?.macroCategoryId

                onUpdateCategoryData(
                    CategoriaUpdateData(
                        categoryId = category.categoryId,
                        newName = if (name != category.categoryName) name else null, // Invia solo se cambiato
                        newMacroCategoryId = if (selectedMacroIdByUser != category.categoryMacroCategoryId) selectedMacroIdByUser else null
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
                currencyFormatter = DecimalFormat("#,##0.00"),
                availableMacroCategories = listOf(
                    MacroCategoryUiModel(1L, "Casa"),
                    MacroCategoryUiModel(2L, "Lavoro"),
                    MacroCategoryUiModel(3L, "Tempo Libero")
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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