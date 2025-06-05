package com.bumbledev.birthdays

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class NotificationPreferences(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "birthday_notification_prefs"
        private const val KEY_SAME_DAY_ENABLED = "same_day_enabled"
        private const val KEY_THREE_DAY_ENABLED = "three_day_enabled"
        private const val KEY_SEVEN_DAY_ENABLED = "seven_day_enabled"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    var sameDayEnabled: Boolean
        get() = prefs.getBoolean(KEY_SAME_DAY_ENABLED, false)
        set(value) = prefs.edit { putBoolean(KEY_SAME_DAY_ENABLED, value) }
    
    var threeDayEnabled: Boolean
        get() = prefs.getBoolean(KEY_THREE_DAY_ENABLED, false)
        set(value) = prefs.edit { putBoolean(KEY_THREE_DAY_ENABLED, value) }
    
    var sevenDayEnabled: Boolean
        get() = prefs.getBoolean(KEY_SEVEN_DAY_ENABLED, false)
        set(value) = prefs.edit { putBoolean(KEY_SEVEN_DAY_ENABLED, value) }
    
    fun getNotificationSettings(): NotificationSettings {
        return NotificationSettings(
            sameDayEnabled = sameDayEnabled,
            threeDayEnabled = threeDayEnabled,
            sevenDayEnabled = sevenDayEnabled
        )
    }
    
    fun saveNotificationSettings(settings: NotificationSettings) {
        sameDayEnabled = settings.sameDayEnabled
        threeDayEnabled = settings.threeDayEnabled
        sevenDayEnabled = settings.sevenDayEnabled
    }
}