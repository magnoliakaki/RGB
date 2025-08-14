package com.bloomtregua.rgb.layout.homepage

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloomtregua.rgb.database.accounts.AccountEntity


@Composable
fun SelezioneConto(
    modifier: Modifier = Modifier,
    currentAccountName: String,
    allAccounts: List<AccountEntity>,
    onAccountSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // Stato per gestire l'espansione della selezione del conto

    val displayText = truncateText(currentAccountName, 32) // Testo per il calcolo della dimensione e visualizzazione
    val dynamicFontSize = dimensioneTestoConto(displayText).sp


    // Box serve come anchor per il DropdownMenu e come area cliccabile
    Box(modifier = modifier) { // Il modifier passato da RiquadroConto si applica qui
        Box(
            modifier = Modifier
                .wrapContentWidth() // Si adatta alla larghezza del testo
                .fillMaxHeight()
                .clickable {
                    expanded = !expanded
                    Log.d("SelezioneConto", "Trigger cliccato. Nuovo stato expanded: $expanded")
                }
                .background(
                    color = Color(0.18f, 0.31f, 0.36f, 1.0f),
                    shape = RoundedCornerShape(15.0.dp)
                )
                .clip(RoundedCornerShape(15.0.dp))
                .padding( horizontal = 16.dp)
            ,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayText, // Usa il testo (potenzialmente troncato)
                style = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = dynamicFontSize
                ),
                maxLines = 1
            )
        }

        // Il DropdownMenu effettivo
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false // Chiudi il menu se l'utente clicca fuori
                Log.d("SelezioneConto", "DropdownMenu dismiss richiesto.")
            },
            modifier = Modifier
                .background(
                    color = Color(0.18f, 0.31f, 0.36f, 0.95f),
                    shape = RoundedCornerShape(12.dp) // << Prova ad aggiungere la forma qui
                )
                .clip(RoundedCornerShape(12.dp))
        ) {
            if (allAccounts.isEmpty()) {
                Log.w("SelezioneConto", "DropdownMenu: allAccounts è vuota.")
                DropdownMenuItem(
                    text = { Text("Nessun conto disponibile", color = Color.White.copy(alpha = 0.7f)) },
                    onClick = { expanded = false }
                )
            } else {
                Log.d("SelezioneConto", "DropdownMenu visibile. Numero conti da mostrare: ${allAccounts.size}")
                allAccounts.forEachIndexed { index, account ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = account.accountName,
                                color = Color.White,
                                fontSize = 16.sp,
                                lineHeight = 22.sp, // Aumenta l'interlinea
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                            )
                        },
                        onClick = {
                            onAccountSelected(account.accountId)
                            expanded = false
                        }
                    )
                    // Aggiungi un Divider se non è l'ultimo elemento
                    if (index < allAccounts.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp), // Padding per non toccare i bordi
                            thickness = 1.5.dp,                    // Spessore sottile
                            color = Color.White.copy(alpha = 0.2f) // Colore del divisore
                        )
                    }
                }
            }
        }
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