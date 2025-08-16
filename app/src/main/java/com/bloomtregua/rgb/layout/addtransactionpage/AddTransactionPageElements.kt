package com.bloomtregua.rgb.layout.addtransactionpage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun TransactionInsertBox(modifier: Modifier = Modifier) {
    var transactionDescription by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf<String?>(null) }

    ConstraintLayout(modifier = modifier) {
        val (insertBox, amount, date) = createRefs()
        val listaContiEsempio = listOf("Hype", "BBVA", "Carta di credito")

        Box(
            Modifier
                .clip(RoundedCornerShape(15.0.dp))
                .size(350.0.dp, 200.0.dp)
                .constrainAs(insertBox) {
                    centerTo(parent)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }){

            TransactionDescriptionInputField(
                value = transactionDescription,
                onValueChange = { transactionDescription = it },
                label = "Descrizione",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )

            AccountsList(
                title = "Conto",
                items = listaContiEsempio, // Use your list of categories
                selectedItem = selectedAccount,
                onItemSelected = { selectedAccount = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
fun TransactionInsertBoxPreview() {
    TransactionInsertBox(modifier = Modifier.fillMaxSize())
}


@Composable
fun TransactionDescriptionInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
    )
}

@Composable
fun <T> AccountsList(
    title: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (item: T, isSelected: Boolean) -> Unit = { item, isSelected ->
        // Default item appearance
        AccountListDefaultItem(item = item, isSelected = isSelected, itemToString = { it.toString() })
    }
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp) // Constrain height, make it scrollable
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(8.dp)
                )
        ) {
            items(items) { item ->
                val isSelected = item == selectedItem
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = isSelected,
                            onClick = { onItemSelected(item) },
                            role = Role.RadioButton // Or Role.Checkbox if multiple selections were allowed
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp) // Padding for each item
                ) {
                    itemContent(item, isSelected)
                }
            }
        }
    }
}

@Composable
fun <T> AccountListDefaultItem(
    item: T,
    isSelected: Boolean,
    itemToString: (T) -> String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp)) // Optional: visual indication
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)

    ) {
        Text(
            text = itemToString(item),
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        // You could add a RadioButton or Checkbox icon here if desired
        if (isSelected) {
            // Example: Add a simple checkmark or indication
            // Icon(Icons.Filled.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
        }
    }
}
