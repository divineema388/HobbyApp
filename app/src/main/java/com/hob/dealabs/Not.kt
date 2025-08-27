package com.hobby.dealabs

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    private const val CHANNEL_ID = "hobby_progress_channel"
    private const val CHANNEL_NAME = "Hobby Progress"
    private const val CHANNEL_DESC = "Notifications for hobby progress tracking"
    
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showProgressNotification(context: Context, hobbyName: String, minutesAdded: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("Hobby Progress!")
            .setContentText("Added $minutesAdded minutes to $hobbyName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(
                System.currentTimeMillis().toInt(),
                notification
            )
        } catch (e: SecurityException) {
            // Handle notification permission not granted
        }
    }
    
    fun showMilestoneNotification(context: Context, hobbyName: String, totalHours: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.star_big_on)
            .setContentTitle("Milestone Reached! 🎉")
            .setContentText("You've spent $totalHours hours on $hobbyName!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(
                (hobbyName + totalHours).hashCode(),
                notification
            )
        } catch (e: SecurityException) {
            // Handle notification permission not granted
        }
    }
    
    fun showReminderNotification(context: Context) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Hobby Time!")
            .setContentText("Don't forget to work on your hobbies today!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(999, notification)
        } catch (e: SecurityException) {
            // Handle notification permission not granted
        }
    }
}