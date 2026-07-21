package it.lucamichetti.mechlabacademy.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "mechlab_settings")

data class AppSettings(
    val theme: String = "SYSTEM",
    val textScale: Float = 1f,
    val selectedYear: Int = 3,
    val weeklyHours: Int = 7,
    val reminderEnabled: Boolean = false,
    val goalDate: String = "",
)

class SettingsRepository(private val context: Context) {
    private object Keys {
        val theme = stringPreferencesKey("theme")
        val scale = floatPreferencesKey("scale")
        val year = intPreferencesKey("year")
        val hours = intPreferencesKey("hours")
        val reminder = booleanPreferencesKey("reminder")
        val goal = stringPreferencesKey("goal")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            theme = preferences[Keys.theme] ?: "SYSTEM",
            textScale = preferences[Keys.scale] ?: 1f,
            selectedYear = preferences[Keys.year] ?: 3,
            weeklyHours = preferences[Keys.hours] ?: 7,
            reminderEnabled = preferences[Keys.reminder] ?: false,
            goalDate = preferences[Keys.goal] ?: "",
        )
    }

    suspend fun setTheme(value: String) {
        val normalized = value.uppercase().takeIf { it in setOf("SYSTEM", "LIGHT", "DARK") } ?: "SYSTEM"
        context.dataStore.edit { it[Keys.theme] = normalized }
    }

    suspend fun setTextScale(value: Float) {
        context.dataStore.edit { it[Keys.scale] = value.coerceIn(0.85f, 1.35f) }
    }

    suspend fun setYear(value: Int) {
        context.dataStore.edit { it[Keys.year] = value.coerceIn(3, 5) }
    }

    suspend fun setWeeklyHours(value: Int) {
        context.dataStore.edit { it[Keys.hours] = value.coerceIn(1, 40) }
    }

    suspend fun setReminder(value: Boolean) {
        context.dataStore.edit { it[Keys.reminder] = value }
    }

    suspend fun setGoalDate(value: String) {
        context.dataStore.edit { it[Keys.goal] = value.trim() }
    }
}
