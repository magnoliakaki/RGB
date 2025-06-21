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
    ), androidx.room.ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["categoryId"],
        childColumns = ["budgetResetCategory"],
        onDelete = androidx.room.ForeignKey.SET_DEFAULT
    )],
    indices = [androidx.room.Index("budgetSurplusCategoryId"),
        androidx.room.Index("budgetResetCategory")
    ]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "budgetId")
    val budgetId: Long = 0,

    @ColumnInfo(name = "budgetName")
    val budgetName: String,

    @ColumnInfo(name = "budgetResetType")
    val budgetResetType: BudgetResetType = BudgetResetType.DATE,

    @ColumnInfo(name = "budgetResetCategory")
    val budgetResetCategory: Long? = null,

    @ColumnInfo(name = "budgetResetDay")
    val budgetResetDay: Int? = null,

    @ColumnInfo(name = "budgetWeekendShift")
    val budgetWeekendShift: Boolean? = null,

    @ColumnInfo(name = "budgetWeekendShiftDirection")
    val budgetWeekendShiftDirection: BudgetWeekendShiftDirection? = null,

    @ColumnInfo(name = "budgetAutomaticAllocation")
    val budgetAutomaticAllocation: Boolean = false,

    @ColumnInfo(name = "budgetSurplusType")
    val budgetSurplusType: BudgetSurplusType = BudgetSurplusType.ROLLOVER,

    @ColumnInfo(name = "budgetSurplusCategoryId")
    val budgetSurplusCategoryId: Long? = null
)
