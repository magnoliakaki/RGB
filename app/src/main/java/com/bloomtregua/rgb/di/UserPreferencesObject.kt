package com.bloomtregua.rgb.di
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferencesKeys {
    val USER_LOCALE_LANGUAGE = stringPreferencesKey("user_locale_language")
    val USER_LOCALE_COUNTRY = stringPreferencesKey("user_locale_country")
    val USER_LOCALE_NUMBER_FORMAT = stringPreferencesKey("user_locale_number_format")
    val USER_LOCALE_DATE_FORMAT = stringPreferencesKey("user_locale_date_format")
    val USER_LOCALE_CURRENCY = stringPreferencesKey("user_locale_currency")

    val ACTIVE_ACCOUNT_ID = longPreferencesKey("active_account_id")
}