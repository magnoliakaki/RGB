package com.bloomtregua.rgb.database.budget

import androidx.annotation.StringRes
import com.bloomtregua.rgb.R

enum class BudgetSurplusType (@StringRes val descriptionRes: Int) {
    ROLLOVER(R.string.budget_surplus_type_rollover),
    RESET(R.string.budget_surplus_type_reset),
    CATEGORY(R.string.budget_surplus_type_category)
}