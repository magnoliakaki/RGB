package com.bloomtregua.rgb.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomtregua.rgb.di.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class UserpreferenceViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository // Iniettato
) : ViewModel() {

    // Flusso base per locale e codice valuta
    private val baseFormattingInfoFlow = kotlinx.coroutines.flow.combine(
        userPreferencesRepository.userLocaleFlow,
        userPreferencesRepository.preferredCurrencyCodeFlow
    ) { locale, currencyCode ->
        Pair(locale, currencyCode)
    }

    // Formattatore CON simbolo valuta
    val currencyFormatterWithSymbol: StateFlow<DecimalFormat> =
        baseFormattingInfoFlow.map { (locale, currencyCode) ->
            val formatter = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
            try {
                val currency = Currency.getInstance(currencyCode)
                formatter.currency = currency

                // Logica aggiuntiva per il simbolo €/$ se necessario
                 val symbols = formatter.decimalFormatSymbols
                 if (currencyCode == "EUR" && (locale.country == "IT" || locale.country == "DE" || locale.country == "FR" || locale.country == "ES")) {
                     symbols.currencySymbol = "€"
                 } else if (currencyCode == "USD" && locale.country == "US") {
                     symbols.currencySymbol = "$"
                 } // Altrimenti usa il simbolo di default del locale

                 formatter.decimalFormatSymbols = symbols
            } catch (e: IllegalArgumentException) {
                Log.e("UserpreferenceVM", "Error setting currency for formatterWithSymbol", e)
                // Fallback a un simbolo generico o USD se il currencyCode non è valido
                try { formatter.currency = Currency.getInstance("USD") } catch (e: Exception) { /* no-op */ }
            }
            formatter
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = run { // Fallback iniziale
                val initialLocale = Locale("it", "IT")
                val formatter = NumberFormat.getCurrencyInstance(initialLocale) as DecimalFormat
                try { formatter.currency = Currency.getInstance("EUR") } catch (e: Exception) { /* no-op */ }
                formatter
            }
        )

    // Formattatore SENZA simbolo valuta
    val currencyFormatterWithoutSymbol: StateFlow<DecimalFormat> =
        baseFormattingInfoFlow.map { (locale, currencyCode) -> // currencyCode qui è solo per consistenza, ma non usato per il simbolo
            val formatter = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
            try {
                // Impostiamo comunque la valuta per la corretta formattazione dei decimali/separatori basati su quella valuta, anche se il simbolo è nascosto.
                val currency = Currency.getInstance(currencyCode)
                formatter.currency = currency // Utile per separatori decimali corretti, ecc.

                val symbols = formatter.decimalFormatSymbols // Crea una nuova istanza modificabile
                symbols.currencySymbol = "" // Rimuovi il simbolo
                formatter.decimalFormatSymbols = symbols
            } catch (e: IllegalArgumentException) {
                Log.e("UserpreferenceVM", "Error setting currency for formatterWithoutSymbol", e)
                try { formatter.currency = Currency.getInstance("USD") } catch (e: Exception) { /* no-op */ }
                val symbols = formatter.decimalFormatSymbols
                symbols.currencySymbol = ""
                formatter.decimalFormatSymbols = symbols
            }
            formatter
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = run { // Fallback iniziale
                val initialLocale = Locale("it", "IT")
                val formatter = NumberFormat.getCurrencyInstance(initialLocale) as DecimalFormat
                try {
                    // Anche se il simbolo è nascosto, impostare la valuta aiuta con i separatori, ecc.
                    formatter.currency = Currency.getInstance("EUR")
                    val symbols = formatter.decimalFormatSymbols
                    symbols.currencySymbol = ""
                    formatter.decimalFormatSymbols = symbols
                } catch (e: Exception) { /* no-op */ }
                formatter
            }
        )

    // --- NUOVO: Flusso per DateTimeFormatter ---
    val dateFormatterFlow: StateFlow<DateTimeFormatter> =
        userPreferencesRepository.userLocaleFlow
            .map { currentLocale ->
                Log.d("UserpreferenceVM", "Creating DateTimeFormatter for locale: $currentLocale")
                DateTimeFormatter.ofPattern("dd/MM/yy", Locale("it", "IT"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("it", "IT")) // Fallback iniziale
            )

}