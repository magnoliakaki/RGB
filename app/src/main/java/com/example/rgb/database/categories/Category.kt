package com.example.rgb.database.categories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rgb.database.accounts.AccountEntity
import com.example.rgb.database.allocations.AllocationEntity

@Entity(tableName = "categories",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = MacroCategoryEntity::class,
            parentColumns = ["MacroCategoryId"],
            childColumns = ["categoryMacroCategoryId"],
            onDelete = androidx.room.ForeignKey.SET_DEFAULT
        ),
        androidx.room.ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["accountId"],
            childColumns = ["categoryAccountId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        ),
        androidx.room.ForeignKey(
            entity = AllocationEntity::class,
            parentColumns = ["allocationId"],
            childColumns = ["categoryAllocationId"],
            onDelete = androidx.room.ForeignKey.SET_DEFAULT
        )
],
    indices = [
        androidx.room.Index("macroCategoryId"),
        androidx.room.Index("accountId"),
        androidx.room.Index("allocationId")]
    )
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "categoryId")
    val categoryId: Int = 0,

    @ColumnInfo(name = "categoryName")
    val categoryName: String,

    @ColumnInfo(name = "categoryIcon")
    val categoryIcon: String,

    @ColumnInfo(name = "categoryMacroCategoryId")
    val categoryMacroCategoryId: Int = 1,

    @ColumnInfo(name = "categoryAccountId")
    val categoryAccountId: Int,

    @ColumnInfo(name = "categoryAllocationId")
    val categoryAllocationId: Int = 1
)
