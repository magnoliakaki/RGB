package com.bloomtregua.rgb.database.budget

import androidx.room.TypeConverter

class BudgetConverters {
    @TypeConverter
    fun fromBudgetResetType(value: BudgetResetType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toBudgetResetType(value: String?): BudgetResetType? {
        return value?.let { BudgetResetType.valueOf(it) }
    }

    @TypeConverter
    fun fromBudgetWeekendShiftDirection(value: BudgetWeekendShiftDirection?): String? {
        return value?.name
    }

    @TypeConverter
    fun toBudgetWeekendShiftDirection(value: String?): BudgetWeekendShiftDirection? {
        return value?.let { BudgetWeekendShiftDirection.valueOf(it) }
    }
}