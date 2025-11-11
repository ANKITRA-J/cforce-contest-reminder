package com.cforce.reminder.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cforce.reminder.data.Contest

class AlarmReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		val id = intent.getLongExtra(EXTRA_ID, -1L)
		val name = intent.getStringExtra(EXTRA_NAME) ?: return
		val url = intent.getStringExtra(EXTRA_URL) ?: return
		if (id == -1L) return
		ContestNotifier.notifyContest(context, id, name, url, reminder = false)
	}

	companion object {
		const val EXTRA_ID = "extra_contest_id"
		const val EXTRA_NAME = "extra_contest_name"
		const val EXTRA_URL = "extra_contest_url"
		const val ACTION = "com.cforce.reminder.ACTION_CONTEST_ALARM"
	}
}


