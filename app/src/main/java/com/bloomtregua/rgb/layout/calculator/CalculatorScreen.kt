package com.bloomtregua.rgb.layout.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var input by remember { mutableStateOf("") }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = input.ifEmpty { "0" },
            fontSize = 48.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )

        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("C", "0", "=", "+")
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            buttons.forEach { row ->
                Row(modifier = Modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { label ->
                        CalculatorButton(
                            label = label,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        ) {
                            when (label) {
                                "C" -> input = ""
                                "=" -> input = evaluateExpression(input)
                                else -> input += label
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CalculatorScreenPreview() {
    CalculatorScreen()
}
