package com.bumbledev.birthdays

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class BirthdayNotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    override fun doWork(): Result {
        val name = inputData.getString("name") ?: return Result.failure()
        val type = inputData.getString("type") ?: return Result.failure()
        val age = inputData.getInt("age", -1).takeIf { it != -1 }
        val days = inputData.getInt("days", -1).takeIf { it != -1 }
        
        val notificationManager = BirthdayNotificationManager(applicationContext)
        notificationManager.showNotification(name, type, age, days)
        
        return Result.success()
    }
}