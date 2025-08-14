package com.bloomtregua.rgb.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomtregua.rgb.database.accounts.AccountEntity
import com.bloomtregua.rgb.di.AccountRepository
import com.bloomtregua.rgb.di.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val accountRepository: AccountRepository // Assicurati di averlo
) : ViewModel() {

    val activeAccountIdFlow: Flow<Long?> = userPreferencesRepository.activeAccountIdFlow
    val allAccountsFlow: Flow<List<AccountEntity>> = accountRepository.getAllAccounts()

    // Per ottenere il nome del conto attivo da mostrare
    @OptIn(ExperimentalCoroutinesApi::class)
    val activeAccountNameFlow: Flow<String> = activeAccountIdFlow
        .flatMapLatest { accountId ->
            if (accountId == null) {
                flowOf("Nessun Conto")
            } else {
                accountRepository.getAccountById(accountId).map { it?.accountName ?: "Sconosciuto" }
            }
        }
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5000), "Caricamento...")


    fun setActiveAccount(accountId: Long) {
        viewModelScope.launch {
            userPreferencesRepository.setActiveAccountId(accountId)
        }
    }
}