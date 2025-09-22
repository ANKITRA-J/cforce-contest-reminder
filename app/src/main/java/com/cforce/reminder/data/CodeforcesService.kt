package com.cforce.reminder.data

import retrofit2.http.GET

interface CodeforcesService {
	@GET("api/contest.list")
	suspend fun getContests(): ApiResponse<List<Contest>>
}
