package com.bloomtregua.rgb.layout.calculator

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorButton(
    label: String,
    modifier: Modifier = Modifier,
    isOperator: Boolean = false, // Nuovo parametro
    onClick: () -> Unit
) {
    // Esempio di come usare isOperator per cambiare il colore
    val backgroundColor = if (isOperator) {
        MaterialTheme.colorScheme.secondaryContainer // Colore per gli operatori
    } else {
        MaterialTheme.colorScheme.surfaceVariant // Colore per i numeri
    }

    androidx.compose.material3.Button( // Assicurati di usare il Button corretto
        onClick = onClick,
        modifier = modifier,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor) // Imposta il colore
    ) {
        Text(text = label, fontSize = 24.sp) // Dimensione del testo per i pulsanti
    }
}