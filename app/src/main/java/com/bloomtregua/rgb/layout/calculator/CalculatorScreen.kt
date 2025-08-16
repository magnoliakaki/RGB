package com.bloomtregua.rgb.layout.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloomtregua.rgb.di.UserPreferencesRepository
import java.util.Locale

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    userPreferencesRepository: UserPreferencesRepository // Make repository optional
) {
    var input by remember { mutableStateOf("") }
    var displayedText by remember { mutableStateOf("0") }
    var equalWasPressed by remember { mutableStateOf(false) }

    val preferredLocale by userPreferencesRepository.userLocaleFlow.collectAsState(initial = Locale.ITALY)

    Column(
        modifier = modifier
            .padding(8.dp), // Padding esterno per l'intero calcolatore
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Text(
            text = displayedText,
            fontSize = 60.sp, // Aumentata un po' la dimensione del testo
            textAlign = TextAlign.End,
            maxLines = 1, // Assicura che stia su una riga (puoi cambiarlo se necessario)
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp) // Più padding verticale per il display
        )

        // Layout principale per i pulsanti: una riga che contiene numeri+punto e operatori
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.Top // Allinea le colonne dei pulsanti in alto
        ) {
            // Colonna per numeri, 'C' e punto decimale
            Column(
                modifier = Modifier.weight(3f), // Occupa 3/4 della larghezza
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                val numberButtons = listOf(
                    listOf("7", "8", "9"),
                    listOf("4", "5", "6"),
                    listOf("1", "2", "3"),
                    listOf("C", "0", ".") // 'C' e '.' ora qui
                )

                numberButtons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        row.forEach { label ->
                            CalculatorButton(
                                label = label,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f) // Mantiene i pulsanti quadrati
                            ) {
                                when (label) {
                                    "C" -> {
                                        if (displayedText.isNotEmpty() && !equalWasPressed) {
                                            input = input.dropLast(1)
                                            displayedText = displayedText.dropLast(1)
                                            if (displayedText.isEmpty()) {
                                                displayedText = "0"
                                                input = ""
                                            }
                                        }
                                        if (equalWasPressed) {
                                            input = ""
                                            displayedText = "0"
                                        }
                                    }
                                    "." -> {
                                        if (!displayedText.contains(".")) { // Aggiungi il punto solo se non già presente
                                            input += label
                                            displayedText += label
                                        }
                                    }
                                    else -> {
                                        if (equalWasPressed) {
                                            input = ""
                                        }

                                        if (input.isEmpty() || (input.isNotEmpty() && input.last() in listOf('+', '-', '*', '/'))) {
                                            displayedText = label
                                        } else {
                                            displayedText += label
                                        }
                                        input += label
                                    }
                                }
                                equalWasPressed = false
                            }
                        }
                    }
                }
            }

            // Colonna per gli operatori e l'uguale
            Column(
                modifier = Modifier
                    .weight(1f) // Occupa 1/4 della larghezza
                    .fillMaxHeight(), // Imposta l'altezza uguale alle altre colonne (0.8f approssima 4/5)
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                val operatorButtons = listOf("+", "-", "*", "/", "=") // 'CE' potrebbe essere 'C'

                operatorButtons.forEach { label ->
                    CalculatorButton(
                        label = label,
                        modifier = Modifier
                            .fillMaxWidth()
                            // Per far sì che 5 pulsanti occupino lo spazio di 4 (con spaziatura)
                            // L'altezza di ogni pulsante operatore sarà leggermente inferiore
                            // rispetto ai pulsanti numerici se si basa solo su weight().
                            // Per un controllo più preciso, potremmo calcolare l'altezza.
                            // Usiamo weight(1f) qui per una distribuzione uniforme dello spazio verticale
                            // all'interno di questa colonna. L'aspectRatio non è usato qui
                            // per permettere altezze diverse.
                            .weight(1f), // Ogni pulsante operatore prende una parte uguale dell'altezza disponibile
                        isOperator = (label != "=") // Opzionale: per stile diverso
                    ) {
                        equalWasPressed = false
                        if (label == "=") {
                            equalWasPressed = true

                            if (input.isNotEmpty()) {
                                input = evaluateExpression(input, preferredLocale)
                                displayedText = input
                            }
                        } else {
                            // Aggiungi l'operatore solo se l'ultimo carattere non è già un operatore
                            // (logica di base, potrebbe essere migliorata)
                            if (input.isNotEmpty() && input.last() !in listOf('+', '-', '*', '/')) {
                                input = evaluateExpression(input, preferredLocale)
                                input += label
                                displayedText = input
                            } else if (input.isNotEmpty() && input.last() in listOf('+', '-', '*', '/')) {
                                input = input.dropLast(1) + label
                                displayedText = input
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp)) // Aggiungi un piccolo spazio in fondo
    }
}
