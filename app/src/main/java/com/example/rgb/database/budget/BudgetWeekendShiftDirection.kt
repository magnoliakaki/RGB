package com.example.rgb.database.budget
import androidx.annotation.StringRes
import com.example.rgb.R

enum class BudgetWeekendShiftDirection (@StringRes val descriptionRes: Int){
    FORWARD(R.string.budget_weekend_shift_direction_forward),
    BACKWARD(R.string.budget_weekend_shift_direction_backward)
}