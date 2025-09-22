package com.cforce.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object Notifications {
	const val CHANNEL_CONTESTS = "contests"

	fun createChannels(context: Context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			val channel = NotificationChannel(
				CHANNEL_CONTESTS,
				"Contest Reminders",
				NotificationManager.IMPORTANCE_HIGH
			)
			channel.description = "Notifications for upcoming Codeforces contests"
			manager.createNotificationChannel(channel)
		}
	}
}
