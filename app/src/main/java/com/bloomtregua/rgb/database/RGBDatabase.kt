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
import androidx.room.withTransaction
import com.bloomtregua.rgb.database.transactions.TransactionConverters
import java.time.LocalDateTime

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
                //context.deleteDatabase("rgb_database")      //TODO DA RIMUOVERE PRIMA DELLA PUBBLICAZIONE O DEI TEST EFFETTIVI SUL DB
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

suspend fun prepopulateDatabase(context: Context, rgbDatabase: RGBDatabase) {
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
            allocationName = context.getString(R.string.allocation_type_transactions)
        )
    )

    val allocationPocketMoney = allocationDao.insertAllocation(AllocationEntity(
            allocationName = context.getString(R.string.allocation_type_pocket_money)
        )
    )

    val allocationOngoing = allocationDao.insertAllocation(AllocationEntity(
            allocationName = context.getString(R.string.allocation_type_ongoing)
        )
    )

    val allocationSomeday = allocationDao.insertAllocation(AllocationEntity(
            allocationName = context.getString(R.string.allocation_type_someday)
        )
    )

    val allocationDeadline = allocationDao.insertAllocation(AllocationEntity(
            allocationName = context.getString(R.string.allocation_type_deadline)
        )
    )

    /*
     * Macro categorie
     */
    val macroCategoryNeeds = macroCategoryDao.insertMacroCategory(MacroCategoryEntity(
            macroCategoryName = context.getString(R.string.macro_category_needs)
        )
    )

    val macroCategoryWishes = macroCategoryDao.insertMacroCategory(MacroCategoryEntity(
            macroCategoryName = context.getString(R.string.macro_category_wishes)
        )
    )

    val macroCategoryMusts = macroCategoryDao.insertMacroCategory(MacroCategoryEntity(
            macroCategoryName = context.getString(R.string.macro_category_musts)
        )
    )

    val macroCategorySavings = macroCategoryDao.insertMacroCategory(MacroCategoryEntity(
            macroCategoryName = context.getString(R.string.macro_category_savings)
        )
    )

    val macroCategorySubs = macroCategoryDao.insertMacroCategory(MacroCategoryEntity(
            macroCategoryName = context.getString(R.string.macro_category_subs)
        )
    )

    /*
     * Conti
     */

    val accountHype = accountDao.insertAccount(AccountEntity(
            accountName = "Hype",
            accountType = AccountType.CHECKING,
            accountBalance = 1234.00
        )
    )

    val accountBBVA = accountDao.insertAccount(AccountEntity(
            accountName = "BBVA",
            accountType = AccountType.SAVINGS,
            accountBalance = 576.22
        )
    )

    accountDao.insertAccount(AccountEntity(
            accountName = "Banco dei paschi di Siena della città di Firenze",
            accountType = AccountType.SAVINGS
        )
    )

    accountDao.insertAccount(AccountEntity(
        accountName = "Cassa di Risparmio di Bologna",
        accountType = AccountType.CHECKING,
        accountBalance = 0.00
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

    val categoryGroceries2 = categoryDao.insertCategory(CategoryEntity(
        categoryName = "Casa",
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

    val categorySavings2 = categoryDao.insertCategory(CategoryEntity(
            categoryName = "Tecnologia",
            categoryAccountId = accountBBVA,
            categoryAllocationId = allocationOngoing,
            categoryMacroCategoryId = macroCategorySavings,
            categoryAllFrequencyMonths = 1,
            categoryAllAmount = 150.10
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
            categoryAllFrequencyMonths = 1,
            categoryAllAmount = 8.10
        )
    )

    val categorySubscription2 = categoryDao.insertCategory(CategoryEntity(
            categoryName = "Animali",
            categoryAccountId = accountHype,
            categoryAllocationId = allocationTransactions,
            categoryMacroCategoryId = macroCategorySubs,
            categoryAllFrequencyMonths = 1
        )
    )

    val categorySubscription3 = categoryDao.insertCategory(CategoryEntity(
            categoryName = "Netflix",
            categoryAccountId = accountHype,
            categoryAllocationId = allocationTransactions,
            categoryMacroCategoryId = macroCategorySubs,
            categoryAllFrequencyMonths = 1,
            categoryAllAmount = 30.00
        )
    )

    val categoryExtra = categoryDao.insertCategory(CategoryEntity(
            categoryName = "Extra",
            categoryAccountId = accountHype,
            categoryAllocationId = allocationTransactions,
            categoryMacroCategoryId = macroCategorySubs,
            categoryAllFrequencyMonths = 1,
            categoryAllAmount = 400.00,
            categoryAllDefaultAmount = 400.00
        )
    )

    val subCategoryAmazon = subcategoryDao.insertSubcategory(
        SubcategoryEntity(
            subcategoryName = "Amazon",
            subcategoryCategoryId = categoryExtra,
            subcategoryAllAmount = 140.00
        )
    )

    val subCategoryDaTavola = subcategoryDao.insertSubcategory(
        SubcategoryEntity(
            subcategoryName = "Giochi da tavolo",
            subcategoryCategoryId = categoryExtra,
            subcategoryAllAmount = 200.00
        )
    )

    val subCategorySteamEpic = subcategoryDao.insertSubcategory(
        SubcategoryEntity(
            subcategoryName = "Steam/Epic Games",
            subcategoryCategoryId = categoryExtra,
            subcategoryAllAmount = 50.00
        )
    )

    val subCategoryExtraCasa = subcategoryDao.insertSubcategory(
        SubcategoryEntity(
            subcategoryName = "Casa",
            subcategoryCategoryId = categoryExtra,
            subcategoryAllAmount = 80.00
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryExtra,
            transactionSubCategoryId = subCategoryAmazon,
            transactionAmount = 5.0,
            transactionDate = LocalDate.of(2025, 8, 10),
            transactionTimestamp = LocalDateTime.of(2025, 8, 10, 12, 51),
            transactionDescription = "Amazon Fermacarte",
            transactionSign = -1
        )
    )

    //Creo 10 transazioni future
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryGroceries,
            transactionAmount = 30.0,
            transactionDate = LocalDate.of(2025, 9, 5),
            transactionTimestamp = LocalDateTime.of(2025, 9, 5, 10, 0),
            transactionDescription = "Spesa supermercato settembre",
            transactionSign = -1,
            transactionDaContabilizzare = true
        )
    )
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categorySavings,
            transactionAmount = 100.0,
            transactionDate = LocalDate.of(2025, 10, 1),
            transactionTimestamp = LocalDateTime.of(2025, 10, 1, 0, 0),
            transactionDescription = "Trasferimento risparmi ottobre",
            transactionSign = -1,
            transactionDaContabilizzare = true
        )
    )
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryTripToPrague,
            transactionAmount = 50.0,
            transactionDate = LocalDate.of(2025, 9, 15),
            transactionTimestamp = LocalDateTime.of(2025, 9, 15, 12, 0),
            transactionDescription = "Pagamento souvenir Praga",
            transactionSign = -1,
            transactionDaContabilizzare = true
        )
    )
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categorySubscription,
            transactionSubCategoryId = subCategoryAmazon, //Uso una subcategoria esistente per esempio
            transactionAmount = 15.99,
            transactionDate = LocalDate.of(2025, 11, 10),
            transactionTimestamp = LocalDateTime.of(2025, 11, 10, 8, 0),
            transactionDescription = "Rinnovo abbonamento Amazon Prime",
            transactionSign = -1,
            transactionDaContabilizzare = true
        )
    )
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryExtra,
            transactionSubCategoryId = subCategorySteamEpic,
            transactionAmount = 49.99,
            transactionDate = LocalDate.of(2025, 12, 20),
            transactionTimestamp = LocalDateTime.of(2025, 12, 20, 18, 30),
            transactionDescription = "Acquisto gioco Steam in saldo",
            transactionSign = -1,
            transactionDaContabilizzare = true
        )
    )
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryGroceries2, // "Casa"
            transactionAmount = 75.0,
            transactionDate = LocalDate.of(2026, 1, 10),
            transactionTimestamp = LocalDateTime.of(2026, 1, 10, 11, 0),
            transactionDescription = "Materiale per pulizie casa",
            transactionSign = -1,
            transactionDaContabilizzare = true
        )
    )


    val categoryIncome = categoryDao.insertCategory(CategoryEntity(
            categoryName = "Entrate",
            categoryAccountId = accountHype,
            categoryIncome = true
        )
    )

    val subCategoryStipendio = subcategoryDao.insertSubcategory(
        SubcategoryEntity(
            subcategoryName = "Stipendio",
            subcategoryCategoryId = categoryIncome
        )
    )

    val subCategoryTricount = subcategoryDao.insertSubcategory(
        SubcategoryEntity(
            subcategoryName = "Tricount",
            subcategoryCategoryId = categoryIncome
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryIncome,
            transactionSubCategoryId = subCategoryStipendio,
            transactionAmount = 1500.0,
            transactionDate = LocalDate.of(2025, 9, 27),
            transactionTimestamp = LocalDateTime.of(2025, 9, 27, 9, 0),
            transactionDescription = "Stipendio Settembre",
            transactionSign = 1,
            transactionDaContabilizzare = true
        )
    )
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryIncome,
            transactionSubCategoryId = subCategoryTricount,
            transactionAmount = 50.0,
            transactionDate = LocalDate.of(2025, 10, 5),
            transactionTimestamp = LocalDateTime.of(2025, 10, 5, 15, 0),
            transactionDescription = "Rimborso cena amici Tricount",
            transactionSign = 1,
            transactionDaContabilizzare = true
        )
    )
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryIncome,
            transactionAmount = 200.0, // Entrata generica senza sottocategoria
            transactionDate = LocalDate.of(2026, 2, 15),
            transactionTimestamp = LocalDateTime.of(2026, 2, 15, 16, 0),
            transactionDescription = "Vendita oggetti usati online",
            transactionSign = 1,
            transactionDaContabilizzare = true
        )
    )

    // Fine transazioni future


    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryGroceries, // ID della categoria "Spesa"
            transactionAmount = 25.50,
            transactionDate = LocalDate.of(2025, 7, 15),
            transactionTimestamp = LocalDateTime.of(2025, 7, 15, 23, 59), // Anno, Mese, Giorno, Ora, Minuto,
            transactionDescription = "Spesa settimanale Esselunga",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categorySubscription3, // ID della categoria "Spesa"
            transactionAmount = 10.0,
            transactionDate = LocalDate.of(2025, 7, 22),
            transactionTimestamp = LocalDateTime.of(2025, 7, 22, 0, 0), // Anno, Mese, Giorno, Ora, Minuto,
            transactionDescription = "Abbonamento Netflix Mio",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categorySubscription3, // ID della categoria "Spesa"
            transactionAmount = 20.0,
            transactionDate = LocalDate.of(2025, 7, 22),
            transactionTimestamp = LocalDateTime.of(2025, 7, 22, 0, 0), // Anno, Mese, Giorno, Ora, Minuto,
            transactionDescription = "Abbonamento Netflix Ilaria",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categorySubscription, // ID della categoria "Abbonamenti"
            transactionAmount = 9.99, // Spesa
            transactionDate = LocalDate.of(2025, 7, 22),
            transactionTimestamp = LocalDateTime.of(2025, 7, 22, 0, 0), // Anno, Mese, Giorno, Ora, Minuto,
            transactionDescription = "Abbonamento Spotify Mensile",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryGroceries, // ID della categoria "Stipendio" (reddito)
            transactionAmount = 85.62,
            transactionDate = LocalDate.of(2025, 8, 2),
            transactionTimestamp = LocalDateTime.of(2025, 8, 2, 0, 0), // Anno, Mese, Giorno, Ora, Minuto,
            transactionDescription = "Spesa Coop",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryTripToPrague, // ID della categoria "Viaggio a Praga"
            transactionAmount = 70.80, // Spesa (es. una rata per il viaggio o una spesa correlata)
            transactionDate = LocalDate.of(2025, 7, 20),
            transactionTimestamp = LocalDateTime.of(2025, 7, 20, 10, 59), // Anno, Mese, Giorno, Ora, Minuto,
            transactionDescription = "Acconto hotel Praga",
            transactionSign = -1
        )
    )

    val orarioSpecificoTestSpesa1 = LocalDateTime.of(2025, 7, 20, 9, 18) // Anno, Mese, Giorno, Ora, Minuto
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryGroceries, // ID della categoria "Spesa"
            transactionAmount = 5.20,
            transactionDate = LocalDate.of(2025, 7, 20),
            transactionTimestamp = orarioSpecificoTestSpesa1,
            transactionDescription = "Caffè e brioche",
            transactionSign = -1
        )
    )

    val orarioSpecificoTestSpesa2 = LocalDateTime.of(2025, 7, 20, 18, 51) // Anno, Mese, Giorno, Ora, Minuto
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryGroceries, // ID della categoria "Spesa"
            transactionAmount = 51.40,
            transactionDate = LocalDate.of(2025, 7, 20),
            transactionTimestamp = orarioSpecificoTestSpesa2,
            transactionDescription = "Spesa Giugno 2025",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryExtra,
            transactionSubCategoryId = subCategoryAmazon,
            transactionAmount = 40.0,
            transactionDate = LocalDate.of(2025, 8, 10),
            transactionTimestamp = LocalDateTime.of(2025, 8, 10, 12, 51),
            transactionDescription = "Amazon Sedia Pieghevole",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryExtra,
            transactionSubCategoryId = subCategoryAmazon,
            transactionAmount = 25.0,
            transactionDate = LocalDate.of(2025, 8, 10),
            transactionTimestamp = LocalDateTime.of(2025, 8, 10, 12, 51),
            transactionDescription = "Amazon Portaspezie",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryExtra,
            transactionSubCategoryId = subCategorySteamEpic,
            transactionAmount = 29.99,
            transactionDate = LocalDate.of(2025, 8, 10),
            transactionTimestamp = LocalDateTime.of(2025, 8, 10, 12, 51),
            transactionDescription = "Steam Simulator",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryExtra,
            transactionSubCategoryId = subCategoryExtraCasa,
            transactionAmount = 29.99,
            transactionDate = LocalDate.of(2025, 8, 10),
            transactionTimestamp = LocalDateTime.of(2025, 8, 10, 12, 51),
            transactionDescription = "Uccidi Erbacce",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryExtra,
            transactionSubCategoryId = subCategoryExtraCasa,
            transactionAmount = 5.99,
            transactionDate = LocalDate.of(2025, 7, 23),
            transactionTimestamp = LocalDateTime.of(2025, 7, 23, 12, 51),
            transactionDescription = "Tovaglia cucina",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryExtra,
            transactionSubCategoryId = subCategorySteamEpic,
            transactionAmount = 39.99,
            transactionDate = LocalDate.of(2025, 8, 10),
            transactionTimestamp = LocalDateTime.of(2025, 8, 12, 10, 51),
            transactionDescription = "Epic Games 2.0 Simulator",
            transactionSign = -1
        )
    )

    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryExtra,
            transactionSubCategoryId = subCategoryDaTavola,
            transactionAmount = 45.50,
            transactionDate = LocalDate.of(2025, 8, 10),
            transactionTimestamp = LocalDateTime.of(2025, 8, 1, 12, 51),
            transactionDescription = "Carcassonne",
            transactionSign = -1
        )
    )

    // TEST stipendio per reset in categoria o sottocategoria
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryIncome,
            transactionSubCategoryId = subCategoryStipendio,
            transactionAmount = 1000.0,
            transactionDate = LocalDate.of(2025, 7, 20),
            transactionTimestamp = LocalDateTime.of(2025, 7, 20, 12, 51),
            transactionDescription = "Stipendo Luglio 2025",
            transactionSign = -1
        )
    )

    // TEST stipendio per reset in categoria o sottocategoria
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryIncome,
            transactionSubCategoryId = subCategoryStipendio,
            transactionAmount = 1000.0,
            transactionDate = LocalDate.of(2025, 6, 28),
            transactionTimestamp = LocalDateTime.of(2025, 6, 28, 0, 0),
            transactionDescription = "Stipendo Giugno 2025",
            transactionSign = -1
        )
    )

    // TEST stipendio per reset in categoria o sottocategoria
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryIncome,
            transactionSubCategoryId = subCategoryTricount,
            transactionAmount = 1000.0,
            transactionDate = LocalDate.of(2025, 6, 28),
            transactionTimestamp = LocalDateTime.of(2025, 6, 28, 0, 0),
            transactionDescription = "Aggiornamento Tricount Giugno 2025",
            transactionSign = -1
        )
    )

    // TEST stipendio per reset in categoria o sottocategoria
    transactionDao.insertTransaction(
        TransactionEntity(
            transactionCategoryId = categoryIncome,
            transactionSubCategoryId = subCategoryTricount,
            transactionAmount = 1000.0,
            transactionDate = LocalDate.of(2025, 6, 28),
            transactionTimestamp = LocalDateTime.of(2025, 7, 30, 14, 20),
            transactionDescription = "Aggiornamento Tricount Luglio 2025",
            transactionSign = -1
        )
    )

    /*
    * Budget
     */

    // TEST budget per reset in categoria
//    budgetDao.insertBudget(BudgetEntity(
//            budgetName = "Test",
//            budgetResetType = com.bloomtregua.rgb.database.budget.BudgetResetType.CATEGORY,
//            budgetResetCategory = categoryIncome,
//            budgetAutomaticAllocation = false,
//            budgetSurplusType = com.bloomtregua.rgb.database.budget.BudgetSurplusType.RESET,
//        )
//    )

    // TEST budget per reset in sottocategoria
    budgetDao.insertBudget(
        BudgetEntity(
            budgetName = "Test",
            budgetResetType = com.bloomtregua.rgb.database.budget.BudgetResetType.CATEGORY,
            budgetResetCategory = null,
            budgetResetSubCategory = subCategoryStipendio,
            budgetAutomaticAllocation = false,
            budgetSurplusType = com.bloomtregua.rgb.database.budget.BudgetSurplusType.RESET,
        )
    )

    // TEST budget per reset sulla DATA
//    budgetDao.insertBudget(
//        BudgetEntity(
//            budgetName = "Test",
//            budgetResetType = com.bloomtregua.rgb.database.budget.BudgetResetType.DATE,
//            budgetResetCategory = null,
//            budgetResetSubCategory = null,
//            budgetResetDay = 21,
//            budgetAutomaticAllocation = false,
//            budgetSurplusType = com.bloomtregua.rgb.database.budget.BudgetSurplusType.RESET,
//        )
//    )
}