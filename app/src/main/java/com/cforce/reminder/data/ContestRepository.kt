package com.cforce.reminder.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class ContestRepository(private val service: CodeforcesService = NetworkModule.service) {
	suspend fun getUpcomingContests(): List<Contest> = withContext(Dispatchers.IO) {
		val resp = service.getContests()
		if (resp.status != "OK" || resp.result == null) return@withContext emptyList()
		val nowEpoch = Clock.System.now().epochSeconds
		resp.result
			.asSequence()
			.filter { it.phase.equals("BEFORE", ignoreCase = true) && (it.startTimeSeconds ?: Long.MAX_VALUE) > nowEpoch }
			.sortedBy { it.startTimeSeconds }
			.toList()
	}
}
