package com.bloomtregua.rgb.di

import android.content.Context
import androidx.datastore.core.DataStore
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
import javax.inject.Inject
import javax.inject.Singleton

// Estensione per creare l'istanza di DataStore a livello di Context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

@Singleton
class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    // Definisci la chiave per l'ID del conto attivo
    private object PreferencesKeys {
        val ACTIVE_ACCOUNT_ID = longPreferencesKey("active_account_id")
    }

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
            preferences[PreferencesKeys.ACTIVE_ACCOUNT_ID] // Restituisce null se non impostato
        }

    // Funzione per salvare l'ID del conto attivo
    suspend fun setActiveAccountId(accountId: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACTIVE_ACCOUNT_ID] = accountId
        }
    }

    // Funzione per cancellare l'ID del conto attivo (se necessario, es. nessun conto selezionato)
    suspend fun clearActiveAccountId() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.ACTIVE_ACCOUNT_ID)
        }
    }

    suspend fun setDefaultActiveAccountIdIfNeeded(defaultAccountId: Long) {
        context.dataStore.edit { preferences ->
            if (preferences[PreferencesKeys.ACTIVE_ACCOUNT_ID] == null) {
                preferences[PreferencesKeys.ACTIVE_ACCOUNT_ID] = defaultAccountId
            }
        }
    }
}
