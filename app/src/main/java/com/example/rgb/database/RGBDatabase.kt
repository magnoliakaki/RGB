package com.example.rgb.database

import android.content.Context
import androidx.activity.result.launch
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rgb.database.accounts.AccountConverters
import com.example.rgb.database.accounts.AccountDao
import com.example.rgb.database.accounts.AccountEntity
import com.example.rgb.database.accounts.AccountType
import com.example.rgb.database.allocations.AllocationDao
import com.example.rgb.database.allocations.AllocationEntity
import com.example.rgb.database.allocations.DateTimeConverters
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
import java.util.concurrent.Executors

@Database(
    entities = [
        AccountEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        MacroCategoryEntity::class,
        SubcategoryEntity::class,
        AllocationEntity::class
    ],
    version = 1,
    exportSchema = true
)

@TypeConverters(AccountConverters::class, DateTimeConverters::class)
abstract class RGBDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun macroCategoryDao(): MacroCategoryDao
    abstract fun subcategoryDao(): SubcategoryDao
    abstract fun allocationDao(): AllocationDao

    companion object {
        @Volatile private var INSTANCE: RGBDatabase? = null

        fun getInstance(context: Context): Any {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                RGBDatabase::class.java,
                                "rgb_database"
                            ).fallbackToDestructiveMigration(true)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
//                            INSTANCE?.let { database ->
//                                // Create a scope. IO dispatcher is good for database operations.
//                                // This scope will live as long as the onCreate method, effectively.
//                                // For longer-lived operations, you'd manage the scope's lifecycle more carefully.
//                                val scope = CoroutineScope(Dispatchers.IO)
//                                scope.launch
//                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}