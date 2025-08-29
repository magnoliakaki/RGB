package com.bloomtregua.rgb.layout.homepage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bloomtregua.rgb.ui.theme.RGBTheme
import com.bloomtregua.rgb.ui.theme.TransazioneEntrataGreen
import com.bloomtregua.rgb.viewmodels.ProssimaTransazioneUiModel
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// Formatter per la data (puoi metterlo in un file di utilità)
val dateFormatterShort: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

@Composable
fun ListaDettaglioProssimeTransazioni(
    prossimeTransazioni: List<ProssimaTransazioneUiModel>,
    onNavigateBack: () -> Unit,
    dateFormatter: DateTimeFormatter,
    currencyFormatter: DecimalFormat, // Passa il formattatore di valuta
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro", modifier = Modifier.padding(end = 16.dp))
            }
            Text(
                text = "Prossime Transazioni",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        if (prossimeTransazioni.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Nessuna transazione futura programmata.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(prossimeTransazioni, key = { it.id }) { transazione ->
                    ProssimaTransazioneItem(
                        dateFormatter = dateFormatter,
                        transazione = transazione,
                        currencyFormatter = currencyFormatter
                    )
                    HorizontalDivider(
                        Modifier,
                        DividerDefaults.Thickness,
                        DividerDefaults.color
                    )
                }
            }
        }
    }
}

@Composable
fun ProssimaTransazioneItem(
    transazione: ProssimaTransazioneUiModel,
    dateFormatter: DateTimeFormatter,
    currencyFormatter: DecimalFormat,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transazione.descrizione.ifEmpty { "Nessuna descrizione" },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Data: ${transazione.data.format(dateFormatter)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (transazione.nomeCategoria != null) {
                Text(
                    text = "Categoria: ${transazione.nomeCategoria}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = currencyFormatter.format(transazione.importo),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (transazione.importo >= 0) TransazioneEntrataGreen else MaterialTheme.colorScheme.error
            // Adatta il colore in base al segno se necessario (se l'importo include già il segno)
        )
    }
}

@Composable
fun DettaglioProssimeTransazioni(
    prossimeTransazioni: List<ProssimaTransazioneUiModel>,
    currencyFormatter: DecimalFormat, // Passa il formattatore di valuta
    modifier: Modifier = Modifier,
    dateFormatter: DateTimeFormatter
) {
    if (prossimeTransazioni.isEmpty()) {
        Box(modifier = modifier
            .fillMaxWidth()
            , contentAlignment = Alignment.Center) {
            Text("Nessuna transazione futura programmata.")
        }
    } else {
        Column(modifier = modifier) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth() // Puoi dare un'altezza fissa o .weight(1f) se in una Column
            ) {
                items(prossimeTransazioni, key = { it.id }) { transazione ->
                    DettaglioTransazioneItem(
                        dateFormatter = dateFormatter,
                        transazione = transazione,
                        currencyFormatter = currencyFormatter
                    )
                }
            }
        }
    }
}

@Composable
fun DettaglioTransazioneItem(
    transazione: ProssimaTransazioneUiModel,
    currencyFormatter: DecimalFormat,
    modifier: Modifier = Modifier,
    dateFormatter: DateTimeFormatter
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val (dataRef, categoriaRef, nomeRef, totaleRef) = createRefs()

        Text(
            text = transazione.data.format(dateFormatter),
            Modifier
                .constrainAs(dataRef) {
                    linkTo(parent.top, parent.bottom)
                    start.linkTo(parent.start)
                },
            style = LocalTextStyle.current.copy(
                color = Color.White,
                textAlign = TextAlign.Left,
                fontSize = 14.0.sp
            )
        )

        // Testo Categoria
        Text(
            text = transazione.nomeCategoria ?: "-",
            Modifier
                .width(75.dp) // Imposta una larghezza fissa per la categoria
                .constrainAs(categoriaRef) {
                    linkTo(parent.top, parent.bottom)
                    start.linkTo(dataRef.end, margin = 16.dp) // Lega l'inizio alla fine della data
                },
            style = LocalTextStyle.current.copy(
                color = Color.White,
                textAlign = TextAlign.Left,
                fontSize = 14.0.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis // Se vuoi i puntini per testo troppo lungo
        )

        // Testo Nome Transazione (Descrizione)
        Text(
            text = transazione.descrizione, // USA I DATI DINAMICI
            Modifier
                .constrainAs(nomeRef) {
                    linkTo(parent.top, parent.bottom)
                    start.linkTo(categoriaRef.end, margin = 8.dp) // Lega l'inizio alla fine della categoria
                    end.linkTo(totaleRef.start, margin = 16.dp)
                    width = Dimension.fillToConstraints
                },
            style = LocalTextStyle.current.copy(
                color = Color.White,
                textAlign = TextAlign.Left,
                fontSize = 14.0.sp,
                fontStyle = FontStyle.Italic
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis // Se vuoi i puntini per testo troppo lungo
        )

        // Testo Totale Transazione
        Text(
            text = currencyFormatter.format(transazione.importo), // USA I DATI DINAMICI
            Modifier
                .constrainAs(totaleRef) {
                    linkTo(parent.top, parent.bottom)
                    end.linkTo(parent.end)
                },
            style = LocalTextStyle.current.copy(
                color = Color.White,
                textAlign = TextAlign.Left,
                fontSize = 14.0.sp,
            )
        )
    }
}

@Preview(showBackground = true, name = "DettaglioProssimeTransazioni Preview")
@Composable
fun DettaglioProssimeTransazioniPreview() {
    val sampleTransactions = listOf(
        ProssimaTransazioneUiModel(1, "Stipendio", 2500.00, LocalDate.now().plusDays(5), "Entrate"),
        ProssimaTransazioneUiModel(2, "Affitto", -850.00, LocalDate.now().plusDays(10), "Casa"),
        ProssimaTransazioneUiModel(3, "Bolletta Luce", -75.50, LocalDate.now().plusDays(15), "Utenze"),
        ProssimaTransazioneUiModel(4, "Cena fuori", -60.00, LocalDate.now().plusDays(2), "Svago"),
        ProssimaTransazioneUiModel(5, "Regalo Compleanno Mamma", -50.00, LocalDate.now().plusDays(20))
    )
    val currencyFormatter = DecimalFormat("#,##0.00 €") // Formattatore di esempio
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) // Formattatore di esempio

    RGBTheme(dynamicColor = false) {
        Surface() {
            ListaDettaglioProssimeTransazioni(
                prossimeTransazioni = sampleTransactions,
                currencyFormatter = currencyFormatter,
                dateFormatter = dateFormatter,
                onNavigateBack = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "DettaglioProssimeTransazioni Vuoto Preview")
@Composable
fun DettaglioProssimeTransazioniVuotoPreview() {
    val emptyTransactions = emptyList<ProssimaTransazioneUiModel>()
    val currencyFormatter = DecimalFormat("#,##0.00 €") // Formattatore di esempio
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) // Formattatore di esempio

    RGBTheme(dynamicColor = false) {
        Surface() {
            ListaDettaglioProssimeTransazioni(
                prossimeTransazioni = emptyTransactions,
                currencyFormatter = currencyFormatter,
                dateFormatter = dateFormatter,
                onNavigateBack = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "DettaglioProssimeTransazioni Preview")
@Composable
fun DettaglioProssimeTransazioniPreview2() {
    val sampleTransactions = listOf(
        ProssimaTransazioneUiModel(1, "Stipendio", 2500.00, LocalDate.now().plusDays(5), "Entrate"),
        ProssimaTransazioneUiModel(2, "Affitto", -850.00, LocalDate.now().plusDays(10), "Casa"),
        ProssimaTransazioneUiModel(3, "Bolletta Luce", -75.50, LocalDate.now().plusDays(15), "Utenze domestiche"),
        ProssimaTransazioneUiModel(4, "Cena fuori", -60.00, LocalDate.now().plusDays(2), "Svago"),
        ProssimaTransazioneUiModel(5, "Regalo Compleanno Mamma", -50.00, LocalDate.now().plusDays(20))
    )
    val currencyFormatter = DecimalFormat("#,##0.00 €") // Formattatore di esempio
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) // Formattatore di esempio

    RGBTheme(dynamicColor = false) {
        Surface() {
            DettaglioProssimeTransazioni(
                prossimeTransazioni = sampleTransactions,
                currencyFormatter = currencyFormatter,
                dateFormatter = dateFormatter
            )
        }
    }
}

@Preview(showBackground = true, name = "DettaglioProssimeTransazioni Vuoto Preview")
@Composable
fun DettaglioProssimeTransazioniVuotoPreview2() {
    val emptyTransactions = emptyList<ProssimaTransazioneUiModel>()
    val currencyFormatter = DecimalFormat("#,##0.00 €") // Formattatore di esempio
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) // Formattatore di esempio

    RGBTheme(dynamicColor = false) {
        Surface() {
            DettaglioProssimeTransazioni(
                prossimeTransazioni = emptyTransactions,
                currencyFormatter = currencyFormatter,
                dateFormatter = dateFormatter
            )
        }
    }
}
