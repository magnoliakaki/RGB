package com.bloomtregua.rgb.database.budget

import androidx.annotation.StringRes
import com.bloomtregua.rgb.R

enum class BudgetSurplusType (@StringRes val descriptionRes: Int) {
    ROLLOVER(R.string.budget_surplus_type_rollover),    // Stessa categoria ma viene aggiunto al mese dopo
    RESET(R.string.budget_surplus_type_reset),          // Azzerato e viene aggiunto al totale da assegnare al mese dopo
    CATEGORY(R.string.budget_surplus_type_category)     // Andrà aggiunto a tale categoria in caso di Surplus
}