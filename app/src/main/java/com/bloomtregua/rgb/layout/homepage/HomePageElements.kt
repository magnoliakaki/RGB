package com.bloomtregua.rgb.layout.homepage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bloomtregua.rgb.R
import com.bloomtregua.rgb.viewmodels.CategoriaUiModel
import com.bloomtregua.rgb.ui.theme.RGBTheme

@Preview(showBackground = true, name = "Anteprima Riquadro Conto")
@Composable
private fun PreviewRiquadroConto() {
    RGBTheme(dynamicColor = false)  {
        Surface(modifier = Modifier
            .fillMaxWidth() // Permetti a Surface di prendere la larghezza
            .height(46.dp), // Altezza sufficiente per vedere qualcosa
        ) {
            RiquadroConto(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(showBackground = true, name = "Anteprima Barra Navigazione")
@Composable
private fun PreviewBarraNavigazione() {
    RGBTheme(dynamicColor = false)  {
        Surface(modifier = Modifier
            .fillMaxWidth() // Permetti a Surface di prendere la larghezza
            .height(46.dp), // Altezza sufficiente per vedere qualcosa
        ) {
            BarraNavigazione(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable()
fun DettaglioProssimeTransazioni(modifier: Modifier = Modifier) {
    ConstraintLayout(modifier = modifier) {
        val (DataTransazione, CategoriaTransazione, NomeTransazione, TotaleTransazione) = createRefs()


        Text("14/06/25", Modifier
            .constrainAs(DataTransazione) {
                linkTo(parent.start, parent.end, bias = 0.0f)
                linkTo(parent.top, parent.bottom, bias = 0.5f)
                width = Dimension.wrapContent
                height = Dimension.percent(1.0f)
            }, style = LocalTextStyle.current.copy(color = Color(1.0f, 1.0f, 1.0f, 1.0f), textAlign = TextAlign.Left, fontSize = 14.0.sp))

        Text("Altro", Modifier
            .constrainAs(CategoriaTransazione) {
                linkTo(parent.start, parent.end, bias = 0.28f)
                linkTo(parent.top, parent.bottom, bias = 0.5f)
                width = Dimension.wrapContent
                height = Dimension.percent(1.0f)
            }, style = LocalTextStyle.current.copy(color = Color(1.0f, 1.0f, 1.0f, 1.0f), textAlign = TextAlign.Left, fontSize = 14.0.sp))

        Text("Rata orecchini", Modifier
            .constrainAs(NomeTransazione) {
                linkTo(parent.start, parent.end, bias = 0.62f)
                linkTo(parent.top, parent.bottom, bias = 0.5f)
                width = Dimension.wrapContent
                height = Dimension.percent(1.0f)
            }, style = LocalTextStyle.current.copy(color = Color(1.0f, 1.0f, 1.0f, 1.0f), textAlign = TextAlign.Left, fontSize = 14.0.sp, fontStyle = FontStyle.Italic))

        Text("- 38.32", Modifier
            .constrainAs(TotaleTransazione) {
                linkTo(parent.start, parent.end, bias = 1.0f)
                linkTo(parent.top, parent.bottom, bias = 0.5f)
                width = Dimension.wrapContent
                height = Dimension.percent(1.0f)
            }, style = LocalTextStyle.current.copy(color = Color(1.0f, 1.0f, 1.0f, 1.0f), textAlign = TextAlign.Right, fontSize = 14.0.sp))

    }
}
@Composable()
fun BarraNavigazione(modifier: Modifier = Modifier) {
    ConstraintLayout(modifier = modifier) {
        val (RettangoloNavigazione, Layers, Plus, Settings) = createRefs()

        Box(
            Modifier
                .clip(RoundedCornerShape(15.0.dp))
                .size(350.0.dp, 46.0.dp)
                .background(Color(0.18f, 0.31f, 0.36f, 0.46f))
                .constrainAs(RettangoloNavigazione) {
                    centerTo(parent)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }){}

        Layers(Modifier.constrainAs(Layers) {
            linkTo(parent.start, parent.end, bias = 0.11f)
            linkTo(parent.top, parent.bottom, bias = 0.5f)
            width = Dimension.percent(0.09f)
            height = Dimension.percent(0.7f)
        })
        Plus(Modifier.constrainAs(Plus) {
            linkTo(parent.start, parent.end, bias = 0.28f)
            linkTo(parent.top, parent.bottom, bias = 0.5f)
            width = Dimension.percent(0.09f)
            height = Dimension.percent(0.7f)
        })
        Settings(Modifier.constrainAs(Settings) {
            linkTo(parent.start, parent.end, bias = 0.92f)
            linkTo(parent.top, parent.bottom, bias = 0.5f)
            width = Dimension.percent(0.09f)
            height = Dimension.percent(0.7f)
        })
    }
}
@Composable()
fun RiquadroConto(modifier: Modifier = Modifier) {
    ConstraintLayout(modifier = modifier) {
        val (RectangleConto, SaldoConto, SelezioneConto, AlertConto) = createRefs()


        Box(
            Modifier
                .clip(RoundedCornerShape(15.0.dp))
                .background(Color(0.18f, 0.31f, 0.36f, 0.46f))
                .constrainAs(RectangleConto) {
                    centerTo(parent)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }){}

        Text("4632.43", Modifier
            .wrapContentHeight(Alignment.CenterVertically)
            .constrainAs(SaldoConto) {
                linkTo(parent.start, parent.end, bias = 0.87f)
                linkTo(parent.top, parent.bottom, bias = 0.5f)
                width = Dimension.percent(0.69f)
                height = Dimension.percent(1.0f)
            }, style = LocalTextStyle.current.copy(color = Color(1.0f, 1.0f, 1.0f, 1.0f), textAlign = TextAlign.Right, fontSize = 22.0.sp))

        SelezioneConto("Hype", Modifier.constrainAs(SelezioneConto) {
            linkTo(parent.start, parent.end, bias = 0.0f)
            linkTo(parent.top, parent.bottom, bias = 0.5f)
            width = Dimension.wrapContent
            height = Dimension.percent(1.0f)
        })

        AlertConto( // Chiamata al tuo Composable AlertConto
            modifier = Modifier.constrainAs(AlertConto) { // Passi il modifier con i vincoli
                start.linkTo(SelezioneConto.end, margin = 8.dp)
                linkTo(parent.top, parent.bottom, bias = 0.5f)
                width = Dimension.percent(0.09f)
                height = Dimension.percent(0.7f)
            }, iconColor = Color.Red
        )
    }
}

@Composable()
fun SelezioneConto(testoConto: String, modifier: Modifier = Modifier) {
    ConstraintLayout(modifier = modifier) { // Il modifier passato da RiquadroConto è applicato qui
        val (Rectangle_7, Hype) = createRefs()

        Box(
            Modifier
                .clip(RoundedCornerShape(15.0.dp))
                // .size(96.0.dp, 46.0.dp) // RIMUOVI QUESTA DIMENSIONE FISSA
                .background(Color(0.18f, 0.31f, 0.36f, 1.0f))
                .constrainAs(Rectangle_7) {
                    centerTo(parent)
                    width =
                        Dimension.fillToConstraints // Il Box si espanderà per riempire SelezioneConto
                    height = Dimension.fillToConstraints// L'altezza del Box riempirà SelezioneConto
                }
        ) {}

        Text(
            text = truncateText(testoConto, 32),
            Modifier
                .padding(horizontal = 10.dp)
                .wrapContentHeight(Alignment.CenterVertically)
                .constrainAs(Hype) {
                    centerTo(parent)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                },
            style = LocalTextStyle.current.copy(
                color = Color(1.0f, 1.0f, 1.0f, 1.0f),
                textAlign = TextAlign.Center,
                fontSize = dimensioneTestoConto(truncateText(testoConto, 32)).sp
            )
        )
    }
}

fun truncateText(text: String, maxLength: Int): String {
    return if (text.length > maxLength) {
        text.substring(0, maxLength - 3) + "..." // Sottrai 3 per fare spazio ai puntini
    } else {
        text
    }
}

fun dimensioneTestoConto(text: String): Double {
    return if (text.length <= 5) {
        24.0
    } else {
        24.0 - (text.length - 5) * (0.75 - 0.05 * (text.length / 6))
    }
}
@Composable()
fun AlertConto(modifier: Modifier = Modifier, iconColor: Color) { // Aggiunto iconColor

    Box(
        modifier = modifier.fillMaxSize(), // Riempi lo spazio dato dal chiamante
        contentAlignment = Alignment.Center // Centra l'icona nel Box
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_alertconto), // Usa l'ID del tuo Vector Asset
            contentDescription = "Alert Icona Conto", // Descrizione per l'accessibilità
            colorFilter = ColorFilter.tint(iconColor) // Permette di colorare l'icona
        )
    }
}
@Composable()
fun ProssimeTransazioni(modifier: Modifier = Modifier) {
    ConstraintLayout(modifier = modifier) {
        val (TitoloTransazioni, ZoomIn) = createRefs()


        Text("Prossime transazioni", Modifier
            .wrapContentHeight(Alignment.Bottom)
            .constrainAs(TitoloTransazioni) {
                linkTo(parent.start, parent.end, bias = 0.0f)
                linkTo(parent.top, parent.bottom, bias = 1.0f)
                width = Dimension.percent(1.0f)
                height = Dimension.percent(0.8f)
            }, style = LocalTextStyle.current.copy(color = Color(0.73f, 0.88f, 0.95f, 1.0f), textAlign = TextAlign.Left, fontSize = 20.0.sp))


        ZoomIn(Modifier.constrainAs(ZoomIn) {
            linkTo(parent.start, parent.end, bias = 0.96f)
            linkTo(parent.top, parent.bottom, bias = 0.93f)
            width = Dimension.percent(0.06f)
            height = Dimension.percent(0.59f)
        })

    }
}
@Composable()
fun Plus(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), // Riempi lo spazio dato dal chiamante
        contentAlignment = Alignment.Center // Centra l'icona nel Box
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_plus), // Usa l'ID del tuo Vector Asset
            contentDescription = "New", // Descrizione per l'accessibilità
        )
    }
}
@Composable()
fun ZoomIn(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), // Riempi lo spazio dato dal chiamante
        contentAlignment = Alignment.Center // Centra l'icona nel Box
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_zoom_in), // Usa l'ID del tuo Vector Asset
            contentDescription = "ZoomIn", // Descrizione per l'accessibilità
        )
    }
}
@Composable()
fun Layers(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), // Riempi lo spazio dato dal chiamante
        contentAlignment = Alignment.Center // Centra l'icona nel Box
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_layers), // Usa l'ID del tuo Vector Asset
            contentDescription = "Layers", // Descrizione per l'accessibilità
        )
    }
}
@Composable()
fun Settings(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), // Riempi lo spazio dato dal chiamante
        contentAlignment = Alignment.Center // Centra l'icona nel Box
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_settings), // Usa l'ID del tuo Vector Asset
            contentDescription = "Settings", // Descrizione per l'accessibilità
        )
    }
}