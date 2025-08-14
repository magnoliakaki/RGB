package com.bloomtregua.rgb.di

import android.content.Context
import androidx.room.Room
import com.bloomtregua.rgb.database.RGBDatabase
import com.bloomtregua.rgb.database.accounts.AccountDao
import com.bloomtregua.rgb.database.budget.BudgetDao
import com.bloomtregua.rgb.database.categories.CategoryDao
import com.bloomtregua.rgb.database.transactions.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Definisce che le dipendenze fornite qui avranno un ciclo di vita a livello di applicazione (singleton)
object DatabaseModule {

    @Provides
    @Singleton // Assicura che ci sia una sola istanza del database nell'intera applicazione
    fun provideRGBDatabase(@ApplicationContext context: Context): RGBDatabase {
        return Room.databaseBuilder(
            context.applicationContext, // Usa context.applicationContext per evitare memory leak
            RGBDatabase::class.java,
            "rgb_database" // Nome del file del database
        )
            // Aggiungi qui i tuoi callback o strategie di migrazione se necessario
            // .addCallback(RGBDatabase.DatabaseCallback( ... )) // Se avevi un callback per il prepopolamento
            .fallbackToDestructiveMigration(false) // ATTENZIONE: per lo sviluppo. Per produzione, implementa migrazioni corrette.
            .build()
    }

    @Provides
    fun provideCategoryDao(database: RGBDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideBudgetDao(database: RGBDatabase): BudgetDao {
        return database.budgetDao()
    }

    @Provides
    fun provideTransactionDao(database: RGBDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    fun provideAccountDao(database: RGBDatabase): AccountDao {
        return database.accountDao()
    }
}

