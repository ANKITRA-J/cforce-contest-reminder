package com.cforce.reminder.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.cforce.reminder.data.Contest
import kotlin.math.max

object AlarmScheduler {
	fun scheduleExact(context: Context, contest: Contest, leadMinutes: Int) {
		val startSec = contest.startTimeSeconds ?: return
		val triggerAtMillis = max(0L, (startSec - (leadMinutes * 60L)) * 1000L)

		val intent = Intent(context, AlarmReceiver::class.java).apply {
			action = AlarmReceiver.ACTION
			putExtra(AlarmReceiver.EXTRA_ID, contest.id)
			putExtra(AlarmReceiver.EXTRA_NAME, contest.name)
			putExtra(AlarmReceiver.EXTRA_URL, contest.url)
		}
		val pending = PendingIntent.getBroadcast(
			context,
			contest.id.toInt(),
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
		val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
			} else {
				am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
			}
		} catch (sec: SecurityException) {
			// On Android 12+ the app may need the user to allow exact alarms.
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
				val request = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
				request.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				context.startActivity(request)
			}
			throw sec
		}
	}
}


