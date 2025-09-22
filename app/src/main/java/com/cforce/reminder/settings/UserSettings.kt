package com.cforce.reminder.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

object Keys {
	val notificationsEnabled = booleanPreferencesKey("notifications_enabled")
	val timezoneId = stringPreferencesKey("timezone_id")
	val leadMinutes = intPreferencesKey("lead_minutes")
}

data class UserSettings(
	val notificationsEnabled: Boolean,
	val timezoneId: String,
	val leadMinutes: Int
)

class SettingsRepository(private val context: Context) {
	val flow: Flow<UserSettings> = context.dataStore.data.map { prefs: Preferences ->
		UserSettings(
			notificationsEnabled = prefs[Keys.notificationsEnabled] ?: true,
			timezoneId = prefs[Keys.timezoneId] ?: java.util.TimeZone.getDefault().id,
			leadMinutes = prefs[Keys.leadMinutes] ?: 30
		)
	}

	suspend fun setNotificationsEnabled(enabled: Boolean) {
		context.dataStore.edit { it[Keys.notificationsEnabled] = enabled }
	}

	suspend fun setTimezone(id: String) {
		context.dataStore.edit { it[Keys.timezoneId] = id }
	}

	suspend fun setLeadMinutes(minutes: Int) {
		context.dataStore.edit { it[Keys.leadMinutes] = minutes }
	}
}
