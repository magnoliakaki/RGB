package com.bloomtregua.rgb.layout.addtransactionpage

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.bloomtregua.rgb.ui.theme.RGBTheme
import com.bloomtregua.rgb.viewmodels.TransactionsViewModel

@Composable()
@Preview(showBackground = true)
fun AddTrasactionPagePreview() {
    RGBTheme(dynamicColor = false) {
        Surface(modifier = Modifier.fillMaxSize()) {
            AddTransactionPage(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable()
fun  AddTransactionPage(modifier: Modifier = Modifier,
                       transactionsViewModel: TransactionsViewModel = hiltViewModel()
) {
// ConstraintLayout itself is not an M3 component, but it hosts M3 components.
    // The background here will come from the parent Surface, or you can set one.
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            // Example: If you want this specific page to have a different background
            // from the main theme's background, you can set it here.
            // .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp) // Example padding
    ){}
}

