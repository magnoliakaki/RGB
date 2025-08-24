package com.bloomtregua.rgb.viewmodels
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomtregua.rgb.database.util.DatabaseTransactionRunner
import com.bloomtregua.rgb.di.AccountRepository
import com.bloomtregua.rgb.di.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class InitialSetupViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val dbTransactionRunner: DatabaseTransactionRunner // Per eseguire operazioni atomiche, quindi si aspetta la fine di tutti gli update prima di fare la commit
) : ViewModel() {

    fun triggerPendingTransactionProcessing() {
        processPendingTransactions()
    }

    private fun processPendingTransactions() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val pendingTransactions = transactionRepository.getPendingTransactionsForAccounting(today)

            if (pendingTransactions.isNotEmpty()) {
                dbTransactionRunner.runInTransaction {
                    for (transaction in pendingTransactions) {
                        val account = accountRepository.getAccountById(transactionRepository.getAccountByTransactionId(transaction.transactionId) ?: 0L).firstOrNull() // Assumendo che tu abbia transactionAccountId
                        if (account != null) {
                            val newBalance = account.accountBalance + (transaction.transactionAmount * transaction.transactionSign) // Applica il segno
                            accountRepository.updateAccountBalance(account.accountId, newBalance)
                            transactionRepository.markTransactionAsAccounted(transaction.transactionId)
                        }
                    }
                }
            }
        }
    }
}