package com.example.rgb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rgb.database.accounts.AccountConverters
import com.example.rgb.database.accounts.AccountDao
import com.example.rgb.database.accounts.AccountEntity
import com.example.rgb.database.categories.CategoryDao
import com.example.rgb.database.categories.CategoryEntity
import com.example.rgb.database.categories.MacroCategoryEntity
import com.example.rgb.database.categories.MacroCategoryDao
import com.example.rgb.database.categories.SubcategoryDao
import com.example.rgb.database.categories.SubcategoryEntity
import com.example.rgb.database.transactions.TransactionDao
import com.example.rgb.database.transactions.TransactionEntity

@Database(
    entities = [
        AccountEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        MacroCategoryEntity::class,
        SubcategoryEntity::class
    ],
    version = 1,
    exportSchema = true
)

@TypeConverters(AccountConverters::class)
abstract class RGBDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun macroCategoryDao(): MacroCategoryDao
    abstract fun subcategoryDao(): SubcategoryDao

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
                            // Pre-populate data here
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}