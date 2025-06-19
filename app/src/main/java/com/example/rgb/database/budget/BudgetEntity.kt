package com.example.rgb.database.budget

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rgb.database.categories.CategoryEntity

@Entity(tableName = "budgets",
    foreignKeys = [androidx.room.ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["categoryId"],
        childColumns = ["budgetSurplusCategoryId"],
        onDelete = androidx.room.ForeignKey.SET_DEFAULT
    )],
    indices = [androidx.room.Index("budgetSurplusCategoryId")]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "budgetId")
    val budgetId: Int = 0,

    @ColumnInfo(name = "budgetName")
    val budgetName: String,

    @ColumnInfo(name = "budgetResetType")
    val budgetResetType: BudgetResetType = BudgetResetType.DATE,

    @ColumnInfo(name = "budgetResetDay")
    val budgetResetDay: Int?,

    @ColumnInfo(name = "budgetWeekendShift")
    val budgetWeekendShift: Boolean?,

    @ColumnInfo(name = "budgetWeekendShiftDirection")
    val budgetWeekendShiftDirection: BudgetWeekendShiftDirection?,

    @ColumnInfo(name = "budgetAutomaticAllocation")
    val budgetAutomaticAllocation: Boolean = false,

    @ColumnInfo(name = "budgetSurplusType")
    val budgetSurplusType: BudgetSurplusType = BudgetSurplusType.ROLLOVER,

    @ColumnInfo(name = "budgetSurplusCategoryId")
    val budgetSurplusCategoryId: Int?
)
