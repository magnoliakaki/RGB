package com.bloomtregua.rgb.layout.homepage

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.bloomtregua.rgb.ui.theme.BarHeightS
import com.bloomtregua.rgb.ui.theme.MarginS
import com.bloomtregua.rgb.ui.theme.MarginXXS
import com.bloomtregua.rgb.ui.theme.PageTopBottomMargins
import com.bloomtregua.rgb.ui.theme.PercentageForPageMid
import com.bloomtregua.rgb.ui.theme.PercentageToPageWidth
import com.bloomtregua.rgb.ui.theme.RGBTheme
import com.bloomtregua.rgb.viewmodels.CategoriesViewModel

@Composable()
@Preview()
fun AndroidPreview_HomePage() {
    RGBTheme(dynamicColor = false)  {
        Surface() {
            HomePage(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable()
fun HomePage(
    modifier: Modifier = Modifier,
    categoriesViewModel: CategoriesViewModel = hiltViewModel()
) {

    // Osserva lo StateFlow delle categorie dal ViewModel
    val categorieState by categoriesViewModel.categoriesUiModel.collectAsState()
    val isLoading by categoriesViewModel.isLoading.collectAsState()

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

        RiquadroConto(Modifier.constrainAs(RiquadroConto) {
            top.linkTo(horizontalTopLGuideLine)
            start.linkTo(verticalLeftGuideline)
            end.linkTo(verticalRightGuideline)
            width = Dimension.fillToConstraints
            height = Dimension.percent(BarHeightS)
        })

        // Elenco categorie
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.constrainAs(LoadingIndicatorRef) {
                centerTo(parent)
            })
        } else {
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
                items(
                    items = categorieState, // Usa la lista dallo StateFlow del ViewModel
                    key = { categoria -> categoria.categoryId }
                ) { categoriaItem ->
                    CategoriaDettaglio(
                        categoria = categoriaItem,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

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

        BarraNavigazione(Modifier.constrainAs(BarraNavigazione) {
            start.linkTo(verticalLeftGuideline)
            end.linkTo(verticalRightGuideline)
            top.linkTo(horizontalBottomGuideLineTransazioni)
            bottom.linkTo(horizontalBottomGuideLine)
            width = Dimension.fillToConstraints
            height = Dimension.percent(BarHeightS)
        })
    }
}
