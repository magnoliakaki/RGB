package com.bloomtregua.rgb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bloomtregua.rgb.R
import com.bloomtregua.rgb.database.accounts.AccountConverters
import com.bloomtregua.rgb.database.accounts.AccountDao
import com.bloomtregua.rgb.database.accounts.AccountEntity
import com.bloomtregua.rgb.database.accounts.AccountType
import com.bloomtregua.rgb.database.allocations.AllocationDao
import com.bloomtregua.rgb.database.allocations.AllocationEntity
import com.bloomtregua.rgb.database.budget.BudgetConverters
import com.bloomtregua.rgb.database.budget.BudgetDao
import com.bloomtregua.rgb.database.budget.BudgetEntity
import com.bloomtregua.rgb.database.categories.CategoryDao
import com.bloomtregua.rgb.database.categories.CategoryEntity
import com.bloomtregua.rgb.database.categories.MacroCategoryEntity
import com.bloomtregua.rgb.database.categories.MacroCategoryDao
import com.bloomtregua.rgb.database.categories.SubcategoryDao
import com.bloomtregua.rgb.database.categories.SubcategoryEntity
import com.bloomtregua.rgb.database.transactions.TransactionDao
import com.bloomtregua.rgb.database.transactions.TransactionEntity
import java.time.LocalDate
import android.util.Log

@Database(
    entities = [
        AccountEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        MacroCategoryEntity::class,
        SubcategoryEntity::class,
        AllocationEntity::class,
        BudgetEntity::class
    ],
    version = 1,
    exportSchema = true
)

@TypeConverters(AccountConverters::class, BudgetConverters::class, TransactionConverters::class)
abstract class RGBDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun macroCategoryDao(): MacroCategoryDao
    abstract fun subcategoryDao(): SubcategoryDao
    abstract fun allocationDao(): AllocationDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile private var INSTANCE: RGBDatabase? = null

        fun getInstance(context: Context): Any {
            Log.d("RGBDatabase", "return instance")
            return INSTANCE ?: synchronized(this) {
                Log.d("RGBDatabase", "Database starting")
                context.deleteDatabase("rgb_database")      //TODO DA RIMUOVERE PRIMA DELLA PUBBLICAZIONE O DEI TEST EFFETTIVI SUL DB
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                RGBDatabase::class.java,
                                "rgb_database"
                            ).fallbackToDestructiveMigration(true)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d("RGBDatabase", "Database created")
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

suspend fun prepopulateDatabase(rgbDatabase: RGBDatabase) {
    val accountDao = rgbDatabase.accountDao()
    val transactionDao = rgbDatabase.transactionDao()
    val categoryDao = rgbDatabase.categoryDao()
    val macroCategoryDao = rgbDatabase.macroCategoryDao()
    val subcategoryDao = rgbDatabase.subcategoryDao()
    val allocationDao = rgbDatabase.allocationDao()
    val budgetDao = rgbDatabase.budgetDao()

    /*
     * Allocazioni
     */

    val allocationTransactions = allocationDao.insertAllocation(AllocationEntity(
            allocationName = R.string.allocation_type_transactions.toString()
        )
    )

    val allocationPocketMoney = allocationDao.insertAllocation(AllocationEntity(
            allocationName = R.string.allocation_type_pocket_money.toString()
        )
    )

    val allocationOngoing = allocationDao.insertAllocation(AllocationEntity(
            allocationName = R.string.allocation_type_ongoing.toString()
        )
    )

    val allocationSomeday = allocationDao.insertAllocation(AllocationEntity(
            allocationName = R.string.allocation_type_someday.toString()
        )
    )

    val allocationDeadline = allocationDao.insertAllocation(AllocationEntity(
            allocationName = R.string.allocation_type_deadline.toString()
        )
    )

    /*
     * Macro categorie
     */

    val macroCategoryNeeds = macroCategoryDao.insertMacroCategory(MacroCategoryEntity(
            macroCategoryName = R.string.macro_category_needs.toString()
        )
    )

    val macroCategoryWishes = macroCategoryDao.insertMacroCategory(MacroCategoryEntity(
            macroCategoryName = R.string.macro_category_wishes.toString()
        )
    )

    val macroCategoryMusts = macroCategoryDao.insertMacroCategory(MacroCategoryEntity(
            macroCategoryName = R.string.macro_category_musts.toString()
        )
    )

    val macroCategorySavings = macroCategoryDao.insertMacroCategory(MacroCategoryEntity(
            macroCategoryName = R.string.macro_category_savings.toString()
        )
    )

    val macroCategorySubs = macroCategoryDao.insertMacroCategory(MacroCategoryEntity(
            macroCategoryName = R.string.macro_category_subs.toString()
        )
    )

    /*
     * Conti
     */

    val accountHype = accountDao.insertAccount(AccountEntity(
            accountName = "Hype",
            accountType = AccountType.CHECKING
        )
    )

    val accountBBVA = accountDao.insertAccount(AccountEntity(
        accountName = "BBVA",
        accountType = AccountType.SAVINGS
    )
    )

    /*
    * Categorie
     */

    val categoryGroceries = categoryDao.insertCategory(CategoryEntity(
            categoryName = "Spesa",
            categoryAccountId = accountHype,
            categoryAllocationId = allocationPocketMoney,
            categoryMacroCategoryId = macroCategoryNeeds,
            categoryAllFrequencyMonths = 1,
            categoryAllDefaultAmount = 100.00,
            categoryAllAmount = 150.00,
        )
    )

    val categorySavings = categoryDao.insertCategory(CategoryEntity(
            categoryName = "Risparmi",
            categoryAccountId = accountBBVA,
            categoryAllocationId = allocationOngoing,
            categoryMacroCategoryId = macroCategorySavings,
            categoryAllFrequencyMonths = 1,
            categoryAllAmount = 150.00
        )
    )

    val categoryTripToPrague = categoryDao.insertCategory(CategoryEntity(
            categoryName = "Viaggio a Praga",
            categoryAccountId = accountBBVA,
            categoryAllocationId = allocationDeadline,
            categoryMacroCategoryId = macroCategoryWishes,
            categoryAllEndDate = LocalDate.of(2025, 12, 31),
            categoryAllEndAmount = 850.00,
            categoryAllAmount = 850.00 / 12,    //TODO: questo sarà fatto nelle logiche del DB, in base alla frequenza.
            categoryAllFrequencyMonths = 1
        )
    )

    val categorySubscription = categoryDao.insertCategory(CategoryEntity(
            categoryName = "Abbonamenti",
            categoryAccountId = accountHype,
            categoryAllocationId = allocationTransactions,
            categoryMacroCategoryId = macroCategorySubs,
            categoryAllFrequencyMonths = 1
        )
    )

    val categoryIncome = categoryDao.insertCategory(CategoryEntity(
            categoryName = "Stipendio",
            categoryAccountId = accountHype,
            categoryIncome = true
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryGroceries, // ID della categoria "Spesa"
            transactionAmount = 25.50,
            transactionDate = LocalDate.of(2024, 7, 15),
            transactionDescription = "Spesa settimanale Esselunga",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categorySubscription, // ID della categoria "Abbonamenti"
            transactionAmount = 9.99, // Spesa
            transactionDate = LocalDate.of(2024, 7, 20),
            transactionDescription = "Abbonamento Spotify Mensile",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryGroceries, // ID della categoria "Stipendio" (reddito)
            transactionAmount = 85.62,
            transactionDate = LocalDate.of(2024, 8, 2),
            transactionDescription = "Spesa Coop",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryTripToPrague, // ID della categoria "Viaggio a Praga"
            transactionAmount = 70.80, // Spesa (es. una rata per il viaggio o una spesa correlata)
            transactionDate = LocalDate.of(2024, 7, 20),
            transactionDescription = "Acconto hotel Praga",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryGroceries, // ID della categoria "Spesa"
            transactionAmount = 5.20,
            transactionDate = LocalDate.of(2024, 7, 22),
            transactionDescription = "Caffè e brioche",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryGroceries, // ID della categoria "Spesa"
            transactionAmount = 51.40,
            transactionDate = LocalDate.of(2024, 6, 26),
            transactionDescription = "Spesa Giugno 2025",
            transactionSign = -1
        )
    )
    /*
    * Budget
     */

    budgetDao.insertBudget(BudgetEntity(
            budgetName = "Test",
            budgetResetType = com.bloomtregua.rgb.database.budget.BudgetResetType.CATEGORY,
            budgetResetCategory = categoryIncome,
            budgetAutomaticAllocation = false,
            budgetSurplusType = com.bloomtregua.rgb.database.budget.BudgetSurplusType.RESET,
        )
    )
}