package com.cforce.reminder.notify

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cforce.reminder.Notifications
import com.cforce.reminder.R

object ContestNotifier {
	fun notifyContest(context: Context, id: Long, name: String, url: String, reminder: Boolean = false) {
		val openIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
		val contentPending = PendingIntent.getActivity(
			context,
			id.toInt(),
			openIntent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		val title = if (reminder) context.getString(R.string.notif_title_reminder) else context.getString(R.string.notif_title_upcoming)
		val text = name

		val builder = NotificationCompat.Builder(context, Notifications.CHANNEL_CONTESTS)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle(title)
			.setContentText(text)
			.setContentIntent(contentPending)
			.setAutoCancel(true)
			.setPriority(NotificationCompat.PRIORITY_HIGH)

		NotificationManagerCompat.from(context).notify(id.toInt() + if (reminder) 100000 else 0, builder.build())
	}
}
