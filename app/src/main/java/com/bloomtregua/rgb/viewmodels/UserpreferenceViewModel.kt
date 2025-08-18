package com.bloomtregua.rgb.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomtregua.rgb.di.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class UserpreferenceViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository // Iniettato
) : ViewModel() {

    val currencyFormatterFlow: StateFlow<DecimalFormat> = combine(
        userPreferencesRepository.userLocaleFlow,
        userPreferencesRepository.preferredCurrencyCodeFlow
    ) { baseFormattingLocale, currencyCode ->
        val formatter = NumberFormat.getCurrencyInstance(baseFormattingLocale) as DecimalFormat

        try {
            val symbols = DecimalFormatSymbols.getInstance(baseFormattingLocale)

            val omettiSimbolo = true
            if (omettiSimbolo) {
                symbols.currencySymbol = ""
                formatter.decimalFormatSymbols = symbols
            } else {
                val currency = Currency.getInstance(currencyCode)
                formatter.currency = currency
                if (currencyCode == "EUR") {
                    symbols.currencySymbol = "€"
                } else {
                    symbols.currencySymbol = "$"
                }
                formatter.decimalFormatSymbols = symbols
            }

        } catch (e: IllegalArgumentException) {
            Log.e("ViewModel", "Error setting currency or symbol", e)
        }
        formatter
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = run {
            val initialLocale = Locale("it", "IT") // Fallback per formattazione
            val formatter = NumberFormat.getCurrencyInstance(initialLocale) as DecimalFormat
            try {
                formatter.currency = Currency.getInstance("USD")
                val symbols = DecimalFormatSymbols.getInstance(initialLocale)
                symbols.currencySymbol = "$"
                formatter.decimalFormatSymbols = symbols
            } catch (e: Exception) { /* no-op */
            }
            formatter
        }
    )

}