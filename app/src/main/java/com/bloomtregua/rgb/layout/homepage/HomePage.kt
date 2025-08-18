package com.bloomtregua.rgb.layout.homepage

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.bloomtregua.rgb.ui.theme.BarHeightS
import com.bloomtregua.rgb.ui.theme.*
import com.bloomtregua.rgb.viewmodels.AccountViewModel
import com.bloomtregua.rgb.viewmodels.CategoriesViewModel
import com.bloomtregua.rgb.viewmodels.UserpreferenceViewModel


@Composable()
fun HomePage(
    modifier: Modifier = Modifier,
    accountViewModel: AccountViewModel = hiltViewModel(),
    categoriesViewModel: CategoriesViewModel = hiltViewModel(),
    userpreferenceViewModel: UserpreferenceViewModel = hiltViewModel()
) {

    //Imposto le impostazioni locali dell'utente
    val currencyFormatter by userpreferenceViewModel.currencyFormatterFlow.collectAsState()

    // Osserva lo StateFlow delle categorie dal ViewModel
    val selectedCategoryId by categoriesViewModel.selectedCategoryId.collectAsState()
    val subCategories by categoriesViewModel.subCategories.collectAsState()
    val categoryDetails by categoriesViewModel.selectedCategoryDetails.collectAsState()
    val categorieState by categoriesViewModel.categoriesUiModel.collectAsState()
    val isLoading by categoriesViewModel.isLoading.collectAsState()

    // Osserva i dati dal ViewModel
    val activeAccountName by accountViewModel.activeAccountNameFlow.collectAsState(initial = "Caricamento...")
    val activeAccountHasAlert = categorieState.find { it.hasErrorInSubcategories }
    val allAccounts by accountViewModel.allAccountsFlow.collectAsState(initial = emptyList())
    val activeAccount = allAccounts.find { it.accountName == activeAccountName }

    ConstraintLayout(modifier = modifier) {
        val (DettaglioProssimeTransazioni, ListaCategorieRef, LoadingIndicatorRef, BarraNavigazione, RiquadroConto, ProssimeTransazioni) = createRefs()

        // LINEE GUIDA VERTICALI
        // margine da sinistra
        val verticalLeftGuideline = createGuidelineFromStart(PercentageToPageWidth)
        // margine da destra
        val verticalRightGuideline = createGuidelineFromEnd(PercentageToPageWidth)
        // centro del composable rispetto a sx/dx
        val verticalCenterGuideLine = createGuidelineFromStart(PercentageForPageMid)

        // LINEE GUIDA ORIZZONTALI
        // margine dall'alto del composable
        val horizontalTopLGuideLine = createGuidelineFromTop(PageTopBottomMargins)
        // margine dal basso del composable
        val horizontalBottomGuideLine = createGuidelineFromBottom(PageTopBottomMargins)
        // centro del composable rispetto a top/bottom
        val horizontalCenterGuideLine = createGuidelineFromTop(PercentageForPageMid)
        // fine dell'elenco delle categorie
        val horizontalBottomGuideLineCategorie = createGuidelineFromTop(0.69f)
        // fine dell'elenco delle transazioni
        val horizontalBottomGuideLineTransazioni = createGuidelineFromTop(0.85f)

        RiquadroConto(
            modifier = Modifier.constrainAs(RiquadroConto) {
                top.linkTo(horizontalTopLGuideLine)
                start.linkTo(verticalLeftGuideline)
                end.linkTo(verticalRightGuideline)
                width = Dimension.fillToConstraints
                height = Dimension.percent(BarHeightS)
            },
            activeAccountName = activeAccountName,
            activeAccountBalance = activeAccount?.accountBalance
                ?: 0.0, // Passo il saldo, o a 0 se non dovesse essere impostato
            allAccounts = allAccounts,
            onAccountSelected = { accountId ->
                accountViewModel.setActiveAccount(accountId)
                categoriesViewModel.clearSelectedCategory()
                categoriesViewModel.setLoadingValue(true)
            },
            hasAlert = activeAccountHasAlert?.hasErrorInSubcategories ?: false,
            onAlertClick = {
                Log.d("HomePage", "Alert icon cliccato!")
            },
            currencyFormatter = currencyFormatter

        )

        // Contenuto Principale: Lista Categorie o Dettagli Categoria
        if (isLoading) { // Loading iniziale per la lista principale
            Box(
                modifier = Modifier.constrainAs(LoadingIndicatorRef) {
                    top.linkTo(RiquadroConto.bottom, margin = MarginS)
                    bottom.linkTo(horizontalBottomGuideLineCategorie)
                    start.linkTo(verticalLeftGuideline)
                    end.linkTo(verticalRightGuideline)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (selectedCategoryId == null) {
            // --- MOSTRA LISTA CATEGORIE PRINCIPALI ---
            LazyColumn(
                modifier = Modifier.constrainAs(ListaCategorieRef) {
                    top.linkTo(RiquadroConto.bottom, margin = MarginS)
                    bottom.linkTo(horizontalBottomGuideLineCategorie)
                    start.linkTo(verticalLeftGuideline)
                    end.linkTo(verticalRightGuideline)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
            ) {
                if (categorieState.isEmpty()) {
                    item {
                        Text(
                            "Nessuna categoria principale trovata.",
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    items(
                        items = categorieState,
                        key = { categoria -> categoria.categoryId }
                    ) { categoriaItem ->
                        // Assumendo che CategoriaDettaglio sia il tuo item per la lista principale
                        CategoriaDettaglio( // Il tuo Composable per un item della lista principale
                            categoria = categoriaItem,
                            modifier = Modifier.fillMaxWidth(),
                            currencyFormatter = currencyFormatter,
                            hasAlertCategoria = categoriaItem.hasErrorInSubcategories,
                            onCategoryClick = { categoryId ->
                                categoriesViewModel.selectCategory(categoryId)
                            }
                        )
                    }
                }
            }
        } else { // selectedCategoryId != null -> MOSTRA DETTAGLI SOTTO-CATEGORIA

            // Se ho selezionato una categoria, allora imposto che si torni alla homepage se si preme il tasto indietro di Android.
            BackHandler(enabled = true) {
                categoriesViewModel.clearSelectedCategory()
            }

            if (categoryDetails == null) {
                Box(
                    modifier = Modifier.constrainAs(LoadingIndicatorRef) {
                        top.linkTo(RiquadroConto.bottom, margin = MarginS)
                        bottom.linkTo(horizontalBottomGuideLineCategorie)
                        start.linkTo(verticalLeftGuideline)
                        end.linkTo(verticalRightGuideline)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {

                LazyColumn(
                    modifier = Modifier.constrainAs(ListaCategorieRef) { // Stessa ref per semplicità
                        top.linkTo(RiquadroConto.bottom, margin = MarginXS)
                        start.linkTo(verticalLeftGuideline)
                        end.linkTo(verticalRightGuideline)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
                ) {

                    categoryDetails?.let { details ->
                        // --- Parte 1: Header/Informazioni Principali ---
                        item {
                            CategoryHeaderSection(
                                category = details,
                                currencyFormatter = currencyFormatter,
                                hasAlertCategoria = details.hasErrorInSubcategories,
                                onNavigateBack = {
                                    categoriesViewModel.clearSelectedCategory()
                                }
                            )
                        }

                        item {
                            androidx.compose.material3.HorizontalDivider(
                                modifier = Modifier.padding(bottom = MarginXS)
                            )
                        }
                    }
                }

            LazyColumn(
                modifier = Modifier.constrainAs(createRef()) { // Stessa ref per semplicità
                    top.linkTo(ListaCategorieRef.bottom)
                    bottom.linkTo(horizontalBottomGuideLineTransazioni)
                    start.linkTo(verticalLeftGuideline)
                    end.linkTo(verticalRightGuideline)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
            ) {

                categoryDetails?.let { details ->

                    // --- Parte 2: Elenco delle Sottocategorie ---
                    if (subCategories.isNotEmpty()) {
                        item {
                            Text(
                                text = "Dettaglio Sottocategorie:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = MarginXXS)
                            )
                        }
                        items(
                            items = subCategories,
                            key = { subCategory -> "sub_${subCategory.subcategoryId}" }
                        ) { subCategoryItem ->
                            SubCategoryListItem(
                                subCategory = subCategoryItem,
                                currencyFormatter = currencyFormatter,
                                onSubCategoryClick = { subCategoryId ->
                                    Log.d(
                                        "HomePage",
                                        "SubCategory clicked: $subCategoryId, Nome: ${subCategoryItem.categoryName}"
                                    )
                                    // categoriesViewModel.selectCategory(subCategoryId) // Per drill-down ulteriore
                                }
                                // Non serve un modifier di padding qui se SubCategoryListItem lo ha già
                            )
                        }
                    } else {
                        item {
                            Text(
                                "Nessuna sottocategoria presente.",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = MarginXS, bottom = MarginXXS)
                            )
                        }
                    }


                    // --- Parte 3: Campi Modificabili ---
                    item {
                        CategoryEditableFieldsSection(
                            category = details,
                            onUpdateCategoryData = { updatedData ->
                                Log.d(
                                    "HomePage",
                                    "Update data for ${details.categoryName}: $updatedData"
                                )
                                // categoriesViewModel.updateCategory(updatedData) // Chiamata al ViewModel
                            },
                            currencyFormatter = currencyFormatter, // Passa il formatter
                            modifier = Modifier.padding(top = 8.dp) // Spazio prima dei campi editabili
                        )
                    }
                }
            }
        }
    }

        if (selectedCategoryId == null) {
            ProssimeTransazioni(Modifier.constrainAs(ProssimeTransazioni) {
                start.linkTo(verticalLeftGuideline)
                end.linkTo(verticalRightGuideline)
                top.linkTo(horizontalBottomGuideLineCategorie, margin = MarginXXS)
                width = Dimension.fillToConstraints
                height = Dimension.percent(0.05f)
            })

            DettaglioProssimeTransazioni(Modifier.constrainAs(DettaglioProssimeTransazioni) {
                top.linkTo(ProssimeTransazioni.bottom, margin = MarginXXS)
                bottom.linkTo(horizontalBottomGuideLineTransazioni)
                start.linkTo(verticalLeftGuideline)
                end.linkTo(verticalRightGuideline)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            })
        }

        BarraNavigazione(
            Modifier.constrainAs(BarraNavigazione) {
                start.linkTo(verticalLeftGuideline)
                end.linkTo(verticalRightGuideline)
                top.linkTo(horizontalBottomGuideLineTransazioni)
                bottom.linkTo(horizontalBottomGuideLine)
                width = Dimension.fillToConstraints
                height = Dimension.percent(BarHeightS)
            },
            onLayersClick = {
                //TODO SOLO PER TEST, DA METTERE POI IL SUO VERO SCOPO E SPOSTARE QUESTA CHIAMATA DOVE SERVE
                categoriesViewModel.notifyDataChanged()
            },
            onPlusCLick = {
            })
    }
}
