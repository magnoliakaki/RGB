package com.bloomtregua.rgb.layout.homepage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bloomtregua.rgb.database.RGBDatabase
import com.bloomtregua.rgb.ui.theme.RGBTheme
import com.bloomtregua.rgb.viewmodels.CategoriesViewModel
import com.bloomtregua.rgb.viewmodels.CategoriesViewModelFactory

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
fun HomePage(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    // Ottieni l'istanza del DAO qui
    val dbRGB = RGBDatabase.getInstance(context.applicationContext) as RGBDatabase
    val categoryDao = dbRGB.categoryDao()

    // Crea il ViewModel usando la factory
    val categoriesViewModel: CategoriesViewModel = viewModel(
        factory = CategoriesViewModelFactory(categoryDao) // Passi il DAO creato localmente
    )

    // Osserva lo StateFlow delle categorie dal ViewModel
    val categorieState by categoriesViewModel.categoriesUiModel.collectAsState()
    val isLoading by categoriesViewModel.isLoading.collectAsState()

    ConstraintLayout(modifier = modifier) {
        val (DettaglioProssimeTransazioni, ListaCategorieRef, LoadingIndicatorRef, BarraNavigazione, RiquadroConto, ProssimeTransazioni) = createRefs()

        RiquadroConto(Modifier.constrainAs(RiquadroConto) {
            linkTo(parent.start, parent.end, bias = 0.52f)
            linkTo(parent.top, parent.bottom, bias = 0.07f)
            width = Dimension.percent(0.85f)
            height = Dimension.percent(0.05f)
        })

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.constrainAs(LoadingIndicatorRef) {
                centerTo(parent)
            })
        } else {
            LazyColumn(
                modifier = Modifier.constrainAs(ListaCategorieRef) {
                    top.linkTo(RiquadroConto.bottom, margin = 32.dp)
                    bottom.linkTo(ProssimeTransazioni.top, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.percent(0.85f)
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
            linkTo(parent.start, parent.end, bias = 0.52f)
            linkTo(parent.top, parent.bottom, bias = 0.68f)
            width = Dimension.percent(0.85f)
            height = Dimension.percent(0.04f)
        })

        DettaglioProssimeTransazioni(Modifier.constrainAs(DettaglioProssimeTransazioni) {
            top.linkTo(ProssimeTransazioni.bottom, margin = 16.dp)
            bottom.linkTo(BarraNavigazione.top, margin = 16.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.percent(0.77f)
            height = Dimension.fillToConstraints
        })

        BarraNavigazione(Modifier.constrainAs(BarraNavigazione) {
            linkTo(parent.start, parent.end, bias = 0.52f)
            linkTo(parent.top, parent.bottom, bias = 0.91f)
            width = Dimension.percent(0.85f)
            height = Dimension.percent(0.05f)
        })
    }
}
