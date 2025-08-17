package com.bloomtregua.rgb.layout.addtransactionpage

import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TransactionDescriptionInputField(modifier : Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = modifier,
        value = text,
        onValueChange = { text = it },
        label = { Text("Descrizione") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDateInputField(
    modifier : Modifier = Modifier,
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    Log.d("DateInputField", "Ricevuta currentDate: $currentDate")
    OutlinedTextField(
        modifier = modifier
            .clickable { showDatePickerDialog = true },
        label = { Text("Data") },
        value = currentDate.format(dateFormatter),
        onValueChange = { },
        readOnly = true,
        enabled = false,
        trailingIcon = {
            Icon(
                Icons.Filled.DateRange,
                contentDescription = "Seleziona Data",
                modifier = Modifier.clickable { showDatePickerDialog = true }
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )


    if (showDatePickerDialog) {
        val calendar = Calendar.getInstance()
        calendar.set(currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth) // Mese è 0-indexed

        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newSelectedDateDialog = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(newSelectedDateDialog)
                showDatePickerDialog = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener { showDatePickerDialog = false }
            show()
        }
    }
}


@Composable
fun TransactionTimeInputField(
    modifier: Modifier = Modifier,
    initialTime: LocalTime = LocalTime.now(), // Pre-valorizzato all'ora attuale
    onTimeSelected: (LocalTime) -> Unit
) {
    var selectedTime by remember { mutableStateOf(initialTime) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Formatter per visualizzare l'ora
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    OutlinedTextField(
        value = selectedTime.format(timeFormatter),
        onValueChange = { /* Non fare nulla, il campo è di sola lettura */ },
        modifier = modifier
            .clickable { showTimePickerDialog = true },
        label = { Text("Ora") },
        readOnly = true,
        enabled = false, // Disabilita l'input testuale mantenendo il clic
        trailingIcon = {
            Icon(
                Icons.Filled.AddCircle,
                contentDescription = "Seleziona Ora",
                modifier = Modifier.clickable { showTimePickerDialog = true }
            )
        },
        // Per far sembrare il campo cliccabile e non disabilitato
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

    if (showTimePickerDialog) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, selectedTime.hour)
        calendar.set(Calendar.MINUTE, selectedTime.minute)

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val newSelectedTime =
                    LocalTime.of(hourOfDay, minute)
                selectedTime = newSelectedTime
                onTimeSelected(newSelectedTime)
                showTimePickerDialog = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // true per formato 24 ore, false per AM/PM
        ).apply {
            setOnDismissListener { showTimePickerDialog = false }
            show()
        }
    }
}

