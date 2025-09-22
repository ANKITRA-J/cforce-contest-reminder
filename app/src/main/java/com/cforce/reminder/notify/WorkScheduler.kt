package com.cforce.reminder.notify

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkScheduler {
	private const val PERIODIC_NAME = "contest_schedule_worker"

	fun schedulePeriodic(context: Context) {
		val constraints = Constraints.Builder()
			.setRequiredNetworkType(NetworkType.CONNECTED)
			.build()
		val request = PeriodicWorkRequestBuilder<ScheduleWorker>(15, TimeUnit.MINUTES)
			.setConstraints(constraints)
			.build()
		WorkManager.getInstance(context).enqueueUniquePeriodicWork(
			PERIODIC_NAME,
			ExistingPeriodicWorkPolicy.UPDATE,
			request
		)
	}

	fun triggerOnce(context: Context) {
		val constraints = Constraints.Builder()
			.setRequiredNetworkType(NetworkType.CONNECTED)
			.build()
		val once = OneTimeWorkRequestBuilder<ScheduleWorker>()
			.setConstraints(constraints)
			.build()
		WorkManager.getInstance(context).enqueueUniqueWork(
			"contest_once",
			ExistingWorkPolicy.REPLACE,
			once
		)
	}
}
