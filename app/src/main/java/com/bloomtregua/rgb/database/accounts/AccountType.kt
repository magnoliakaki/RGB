package com.bloomtregua.rgb.database.accounts
import androidx.annotation.StringRes
import com.bloomtregua.rgb.R

enum class AccountType(@StringRes val descriptionRes: Int) {
    CHECKING(R.string.account_type_checking),
    SAVINGS(R.string.account_type_saving),
    CREDIT_CARD(R.string.account_type_credit);
}