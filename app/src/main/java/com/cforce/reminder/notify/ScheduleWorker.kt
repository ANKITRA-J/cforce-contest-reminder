package com.cforce.reminder.notify

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cforce.reminder.data.ContestRepository
import com.cforce.reminder.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

class ScheduleWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
	private val repo = ContestRepository()
	private val settings = SettingsRepository(appContext)

	override suspend fun doWork(): Result {
		val user = settings.flow.first()
		if (!user.notificationsEnabled) return Result.success()

		val now = Clock.System.now().epochSeconds
		val upcoming = repo.getUpcomingContests().take(5)
		for (c in upcoming) {
			val start = c.startTimeSeconds ?: continue
			val lead = user.leadMinutes * 60L
			val timeToStart = start - now
			if (timeToStart in 0..lead) {
				ContestNotifier.notifyContest(applicationContext, c.id, c.name, c.url, reminder = false)
			}

			// Ensure an exact alarm is scheduled for the contest using current lead time.
			AlarmScheduler.scheduleExact(applicationContext, c, user.leadMinutes)

			// If contest is ongoing and user ignored, send reminder once during contest
			val duration = c.durationSeconds
			if (now in start..(start + duration)) {
				ContestNotifier.notifyContest(applicationContext, c.id, c.name, c.url, reminder = true)
			}
		}
		return Result.success()
	}
}
