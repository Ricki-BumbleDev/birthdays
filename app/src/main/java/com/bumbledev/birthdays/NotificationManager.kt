package com.bumbledev.birthdays

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class BirthdayNotificationManager(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID = "birthday_notifications"
        const val SAME_DAY_WORK_TAG = "birthday_same_day"
        const val THREE_DAY_WORK_TAG = "birthday_three_day"
        const val SEVEN_DAY_WORK_TAG = "birthday_seven_day"
        const val NOTIFICATION_TIME_HOUR = 9
        const val NOTIFICATION_TIME_MINUTE = 0
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notification_channel_description)
        }
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    
    fun scheduleNotifications(birthdays: List<Birthday>, settings: NotificationSettings) {
        cancelAllNotifications()
        
        birthdays.forEach { birthday ->
            if (settings.sameDayEnabled) {
                scheduleSameDayNotification(birthday)
            }
            if (settings.threeDayEnabled) {
                scheduleAdvanceNotification(birthday, 3, THREE_DAY_WORK_TAG)
            }
            if (settings.sevenDayEnabled) {
                scheduleAdvanceNotification(birthday, 7, SEVEN_DAY_WORK_TAG)
            }
        }
    }
    
    private fun scheduleSameDayNotification(birthday: Birthday) {
        val notificationTime = getNextNotificationTime(birthday.daysUntil)
        if (notificationTime != null) {
            val workRequest = OneTimeWorkRequestBuilder<BirthdayNotificationWorker>()
                .setInitialDelay(getDelayUntil(notificationTime), TimeUnit.MILLISECONDS)
                .setInputData(workDataOf(
                    "name" to birthday.name,
                    "type" to "same_day",
                    "age" to birthday.nextAge
                ))
                .addTag(SAME_DAY_WORK_TAG)
                .build()
            
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
    
    private fun scheduleAdvanceNotification(birthday: Birthday, daysAdvance: Int, tag: String) {
        val targetDaysUntil = birthday.daysUntil - daysAdvance
        if (targetDaysUntil >= 0) {
            val notificationTime = getNextNotificationTime(targetDaysUntil)
            if (notificationTime != null) {
                val workRequest = OneTimeWorkRequestBuilder<BirthdayNotificationWorker>()
                    .setInitialDelay(getDelayUntil(notificationTime), TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf(
                        "name" to birthday.name,
                        "type" to "${daysAdvance}_day_advance",
                        "days" to daysAdvance,
                        "age" to birthday.nextAge
                    ))
                    .addTag(tag)
                    .build()
                
                WorkManager.getInstance(context).enqueue(workRequest)
            }
        }
    }
    
    private fun getNextNotificationTime(daysUntil: Int): LocalDateTime? {
        val today = LocalDate.now()
        val targetDate = today.plusDays(daysUntil.toLong())
        val notificationTime = targetDate.atTime(NOTIFICATION_TIME_HOUR, NOTIFICATION_TIME_MINUTE)
        
        return if (notificationTime.isAfter(LocalDateTime.now())) {
            notificationTime
        } else {
            null
        }
    }
    
    private fun getDelayUntil(targetTime: LocalDateTime): Long {
        return ChronoUnit.MILLIS.between(LocalDateTime.now(), targetTime)
    }
    
    fun cancelAllNotifications() {
        WorkManager.getInstance(context).cancelAllWorkByTag(SAME_DAY_WORK_TAG)
        WorkManager.getInstance(context).cancelAllWorkByTag(THREE_DAY_WORK_TAG)
        WorkManager.getInstance(context).cancelAllWorkByTag(SEVEN_DAY_WORK_TAG)
    }
    
    fun showNotification(name: String, type: String, age: Int?, days: Int?) {
        val title = when (type) {
            "same_day" -> context.getString(R.string.birthday_today_celebration)
            "3_day_advance" -> context.getString(R.string.three_day_notifications_title)
            "7_day_advance" -> context.getString(R.string.seven_day_notifications_title)
            else -> context.getString(R.string.notification_birthday_reminder)
        }
        
        val message = when (type) {
            "same_day" -> if (age != null) {
                context.getString(R.string.notification_birthday_today, name, age)
            } else {
                context.getString(R.string.notification_birthday_today_no_age, name)
            }
            else -> if (age != null) {
                context.getString(R.string.notification_birthday_in_days, name, age, days)
            } else {
                context.getString(R.string.notification_birthday_in_days_no_age, name, days)
            }
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        val notificationId = (name + type).hashCode()
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}

data class NotificationSettings(
    val sameDayEnabled: Boolean = true,
    val threeDayEnabled: Boolean = false,
    val sevenDayEnabled: Boolean = false
) {
    fun hasAnyEnabled(): Boolean = sameDayEnabled || threeDayEnabled || sevenDayEnabled
}