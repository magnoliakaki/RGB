package com.bloomtregua.rgb.layout.calculator

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (label in listOf("+", "-", "*", "/", "=")) Color(0xFF1976D2) else Color(0xFFE0E0E0),
            contentColor = if (label in listOf("+", "-", "*", "/", "=")) Color.White else Color.Black
        )
    ) {
        Text(text = label, fontSize = 24.sp)
    }
}