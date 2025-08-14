package com.bloomtregua.rgb.di

import com.bloomtregua.rgb.database.accounts.AccountDao
import com.bloomtregua.rgb.database.accounts.AccountEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface AccountRepository {
    fun getAllAccounts(): Flow<List<AccountEntity>>
    suspend fun getAccountById(accountId: Long): Flow<AccountEntity?>
}

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    override fun getAllAccounts(): Flow<List<AccountEntity>> {
        return accountDao.getAllAccounts()
    }

    override suspend fun getAccountById(accountId: Long): Flow<AccountEntity?> {
        return accountDao.getAccountById(accountId.toInt())
    }
}
