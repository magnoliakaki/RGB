package com.example.rgb.database.budget

import androidx.annotation.StringRes
import com.example.rgb.R

enum class BudgetResetType(@StringRes val descriptionRes: Int) {
    DATE(R.string.budget_reset_type_date),
    CATEGORY(R.string.budget_reset_type_category),
}