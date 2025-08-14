package com.bloomtregua.rgb.layout.addtransactionpage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bloomtregua.rgb.database.RGBDatabase
import com.bloomtregua.rgb.viewmodels.TransactionsViewModel

@Composable()
fun AddTransactionPage(modifier: Modifier = Modifier,
                       transactionsViewModel: TransactionsViewModel = hiltViewModel()){
    val context = LocalContext.current
    // Ottieni l'istanza del DAO qui
    val dbRGB = RGBDatabase.getInstance(context.applicationContext) as RGBDatabase
    val transactionDao = dbRGB.transactionDao()
}