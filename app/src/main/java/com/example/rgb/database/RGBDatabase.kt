package com.example.rgb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rgb.R
import com.example.rgb.database.accounts.AccountConverters
import com.example.rgb.database.accounts.AccountDao
import com.example.rgb.database.accounts.AccountEntity
import com.example.rgb.database.accounts.AccountType
import com.example.rgb.database.allocations.AllocationDao
import com.example.rgb.database.allocations.AllocationEntity
import com.example.rgb.database.budget.BudgetConverters
import com.example.rgb.database.budget.BudgetDao
import com.example.rgb.database.budget.BudgetEntity
import com.example.rgb.database.categories.CategoryDao
import com.example.rgb.database.categories.CategoryEntity
import com.example.rgb.database.categories.MacroCategoryEntity
import com.example.rgb.database.categories.MacroCategoryDao
import com.example.rgb.database.categories.SubcategoryDao
import com.example.rgb.database.categories.SubcategoryEntity
import com.example.rgb.database.transactions.TransactionDao
import com.example.rgb.database.transactions.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            categoryAllAmount = 150.00
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

    /*
    * Budget
     */

    val budgetTest = budgetDao.insertBudget(BudgetEntity(
            budgetName = "Test",
            budgetResetType = com.example.rgb.database.budget.BudgetResetType.CATEGORY,
            budgetResetCategory = categoryIncome,
            budgetAutomaticAllocation = false,
            budgetSurplusType = com.example.rgb.database.budget.BudgetSurplusType.RESET,
        )
    )
}