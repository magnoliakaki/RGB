package com.bloomtregua.rgb.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

// Estensione per creare l'istanza di DataStore a livello di Context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

@Singleton
class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    // Flow per osservare l'ID del conto attivo
    val activeAccountIdFlow: Flow<Long?> = context.dataStore.data
        .catch { exception ->
            // Gestisci IOException (o altri errori) leggendo da DataStore
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[UserPreferencesKeys.ACTIVE_ACCOUNT_ID] // Restituisce null se non impostato
        }

    // Funzione per salvare l'ID del conto attivo
    suspend fun setActiveAccountId(accountId: Long) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.ACTIVE_ACCOUNT_ID] = accountId
        }
    }

    // Funzione per cancellare l'ID del conto attivo (se necessario, es. nessun conto selezionato)
    suspend fun clearActiveAccountId() {
        context.dataStore.edit { preferences ->
            preferences.remove(UserPreferencesKeys.ACTIVE_ACCOUNT_ID)
        }
    }

    suspend fun setDefaultActiveAccountIdIfNeeded(defaultAccountId: Long) {
        context.dataStore.edit { preferences ->
            if (preferences[UserPreferencesKeys.ACTIVE_ACCOUNT_ID] == null) {
                preferences[UserPreferencesKeys.ACTIVE_ACCOUNT_ID] = defaultAccountId
            }
        }
    }

    // Setto i parametri di default del locale
    val userLocaleFlow: Flow<Locale> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // TODO: cambia valore di default su italia
            val language = preferences[UserPreferencesKeys.USER_LOCALE_LANGUAGE] ?: Locale.ITALY.language
            val country = preferences[UserPreferencesKeys.USER_LOCALE_COUNTRY] ?: Locale.ITALY.country
            Locale(language, country)
        }

    // Imposto la valuta del Locale (che sarà differente nel caso da quella impostata sul Locale sopra)
    val preferredCurrencyCodeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            val currencyCode = "EUR"
            currencyCode.toString()
        }
        .catch { emit("EUR") } // Fallback

    suspend fun updateUserLocale(locale: Locale) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.USER_LOCALE_LANGUAGE] = locale.language
            preferences[UserPreferencesKeys.USER_LOCALE_COUNTRY] = locale.country
            preferences[UserPreferencesKeys.USER_LOCALE_NUMBER_FORMAT] = locale.country
            preferences[UserPreferencesKeys.USER_LOCALE_DATE_FORMAT] = "default"
        }
    }
}
