package com.bloomtregua.rgb.layout.homepage

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bloomtregua.rgb.ui.theme.*
import com.bloomtregua.rgb.viewmodels.CategoriaUiModel
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

@Preview(showBackground = true, name = "Item Categoria Singola")
@Composable
private fun PreviewCategoriaDettaglio() {
    RGBTheme(dynamicColor = false)  {
        Surface() {
            CategoriaDettaglio(
                categoria = CategoriaUiModel( // Fornisci dati di esempio
                    categoryId = 1L,
                    categoryName = "Supermercato",
                    categoryAllAmount = 250.0,
                    totaleSpeso = 120.50,
                    totaleResiduo = 129.50,
                    percentualeSpeso = 120.50 / 250.0, // Assicurati che il modello lo abbia
                    categoryMacroCategoryId = 10L
                ),
                modifier = Modifier.fillMaxWidth(),
                onCategoryClick = { },
                onSubCategoryClick = { },
                hasAlertCategoria = false,
                currencyFormatter = NumberFormat.getCurrencyInstance(Locale.ITALY) as DecimalFormat
            )
        }

    }
}

@Preview(showBackground = true, name = "Item Categoria - Nome Lungo")
@Composable
private fun PreviewCategoriaDettaglioNomeLungo() {
    RGBTheme(dynamicColor = false)  {
        Surface() {
            CategoriaDettaglio(
                categoria = CategoriaUiModel(
                    categoryId = 2L,
                    categoryName = "Affitto e Spese Condominiali Mensili della Casa al Mare",
                    categoryAllAmount = 800.0,
                    totaleSpeso = 800.0,
                    totaleResiduo = 0.0,
                    percentualeSpeso = 1.0,
                    categoryMacroCategoryId = 11L
                ),
                modifier = Modifier.fillMaxWidth(),
                onCategoryClick = { },
                onSubCategoryClick = { },
                hasAlertCategoria = false,
                currencyFormatter = NumberFormat.getCurrencyInstance(Locale.ITALY) as DecimalFormat
            )
        }
    }
}

@Preview(showBackground = true, name = "Item Categoria - Spesa Eccessiva")
@Composable
private fun PreviewCategoriaDettaglioSpesaEccessiva() {
    RGBTheme(dynamicColor = false)  {
        Surface() {
            CategoriaDettaglio(
                categoria = CategoriaUiModel(
                    categoryId = 3L,
                    categoryName = "Ristoranti",
                    categoryAllAmount = 150.0,
                    totaleSpeso = 180.0, // Spesa > Budget
                    totaleResiduo = -30.0,
                    percentualeSpeso = 180.0 / 150.0,
                    categoryMacroCategoryId = 12L
                ),
                modifier = Modifier.fillMaxWidth(),
                onCategoryClick = { },
                onSubCategoryClick = { },
                hasAlertCategoria = true,
                currencyFormatter = NumberFormat.getCurrencyInstance(Locale.ITALY) as DecimalFormat
            )
        }
    }
}

@Composable()
fun CategoriaDettaglio(
    categoria: CategoriaUiModel, // Passa l'oggetto dati
    modifier: Modifier = Modifier,
    onCategoryClick: (Long) -> Unit,
    onSubCategoryClick: (Long?) -> Unit,
    hasAlertCategoria : Boolean,
    currencyFormatter: DecimalFormat
) {
    ConstraintLayout(
        modifier = modifier
            .padding(bottom = 8.dp) // Aggiungi padding tra gli elementi
            .clickable { onCategoryClick(categoria.categoryId); onSubCategoryClick(categoria.subcategoryId) } // Rendi l'intero elemento cliccabile

    ) {
        val (nomeCategoria, totaleResiduoCategoria, totaleSpesoCategoria, totaleCategoria, spesoCategoria, alertCategoria) = createRefs()

        // Barra di progresso
        Box(
            Modifier
                .background(BarraCategoriaSfondo) // Colore base della barra
                .constrainAs(spesoCategoria) {
                    linkTo(parent.start, parent.end, bias = 0.0f) // Allinea a sinistra
                    top.linkTo(parent.top, margin = 4.dp) // Leggero margine dall'alto
                    width = Dimension.fillToConstraints // Occupa tutta la larghezza disponibile
                    height = Dimension.value(11.dp) // Altezza fissa per la barra
                }
        ) {
            val percentualeSpeso = categoria.percentualeSpeso.toFloat().coerceIn(0f, 1f)
            val coloreSpeso = if (categoria.percentualeSpeso > 1.0f) {
                BarraCategoriaErrore
            } else {
                if (categoria.percentualeSpeso == 1.0) {
                    BarraCategoriaCompleta
                } else {
                    BarraCategoriaProgresso
                }
            }

            Box( // Barra che rappresenta la spesa effettiva
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentualeSpeso) // La larghezza è la percentuale spesa
                    .background(coloreSpeso) // Colore per la parte spesa
            )
        }

        // 2. Nome Categoria
        Text(
            text = categoria.categoryName,
            modifier = Modifier
                .constrainAs(nomeCategoria) {
                    top.linkTo(spesoCategoria.bottom, margin = 6.dp)
                    start.linkTo(parent.start)
                    end.linkTo(totaleSpesoCategoria.start, margin = 8.dp, goneMargin = 0.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                }
            ,
            style = LocalTextStyle.current.copy(color = Color.White, textAlign = TextAlign.Left, fontSize = 20.0.sp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = currencyFormatter.format(categoria.totaleResiduo),
            modifier = Modifier
                .constrainAs(totaleResiduoCategoria) {
                    end.linkTo(parent.end)
                    baseline.linkTo(nomeCategoria.baseline)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
            ,
            style = LocalTextStyle.current.copy(color = Color.White, textAlign = TextAlign.Right, fontSize = 18.0.sp)
        )

        Text(
            text = currencyFormatter.format(categoria.totaleSpeso),
            modifier = Modifier
                .constrainAs(totaleSpesoCategoria) {
                    end.linkTo(parent.end, margin = 120.dp)
                    baseline.linkTo(nomeCategoria.baseline)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
            ,
            style = LocalTextStyle.current.copy(color = Color.White, textAlign = TextAlign.Right, fontSize = 18.0.sp)
        )

        Text(
            text = currencyFormatter.format(categoria.categoryAllAmount),
            modifier = Modifier
                .constrainAs(totaleCategoria) {
                    top.linkTo(nomeCategoria.bottom, margin = 2.dp) // Sotto NomeCategoria
                    start.linkTo(parent.start) // Allineato a sinistra come NomeCategoria
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
            ,
            style = LocalTextStyle.current.copy(color = Color.White, textAlign = TextAlign.Left, fontSize = 12.0.sp, fontStyle = FontStyle.Italic)
        )

        if (hasAlertCategoria && categoria.percentualeSpeso <= 1.0f) { // Mostra l'alert solo se necessario
            AlertConto(
                modifier = Modifier
                    .constrainAs(alertCategoria) {
                        start.linkTo(totaleCategoria.end, margin = 8.dp)
                        top.linkTo(nomeCategoria.bottom, margin = 2.dp)
                        bottom.linkTo(totaleCategoria.bottom)

                        width = Dimension.wrapContent
                        height = Dimension.value(16.dp)
                    },
                    //.clickable(onClick = onAlertClick), // Rendi cliccabile l'alert
                iconColor = Color.Red
            )
        }
    }
}