package com.example.rgb.database.categories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rgb.database.accounts.AccountEntity
import com.example.rgb.database.allocations.AllocationEntity
import java.time.LocalDate

@Entity(tableName = "categories",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = MacroCategoryEntity::class,
            parentColumns = ["macroCategoryId"],
            childColumns = ["categoryMacroCategoryId"],
            onDelete = androidx.room.ForeignKey.SET_NULL
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
        androidx.room.Index("categoryMacroCategoryId"),
        androidx.room.Index("categoryAccountId"),
        androidx.room.Index("categoryAllocationId")]
    )
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "categoryId")
    val categoryId: Long = 0,

    @ColumnInfo(name = "categoryName")
    val categoryName: String,

    @ColumnInfo(name = "categoryIncome")
    val categoryIncome: Boolean = false,

    @ColumnInfo(name = "categoryIcon")
    val categoryIcon: String? = null,

    @ColumnInfo(name = "categoryMacroCategoryId")
    val categoryMacroCategoryId: Long? = null,

    @ColumnInfo(name = "categoryAccountId")
    val categoryAccountId: Long,

    @ColumnInfo(name = "categoryAllocationId")
    val categoryAllocationId: Long? = null,

    @ColumnInfo(name = "categoryAllEndDate")
    val categoryAllEndDate: LocalDate? = null,

    @ColumnInfo(name = "categoryAllEndAmount")
    val categoryAllEndAmount: Double? = null,

    @ColumnInfo(name = "categoryAlloAmount")
    val categoryAllAmount: Double? = null,

    @ColumnInfo(name = "categoryAllFrequencyDays")
    val categoryAllFrequencyDays: Int? = null,

    @ColumnInfo(name = "categoryAllFrequencyWeeks")
    val categoryAllFrequencyWeeks: Int? = null,

    @ColumnInfo(name = "categoryAllFrequencyMonths")
    val categoryAllFrequencyMonths: Int? = null,

    @ColumnInfo(name = "categoryAllFrequencyYears")
    val categoryAllFrequencyYears: Int? = null
)
