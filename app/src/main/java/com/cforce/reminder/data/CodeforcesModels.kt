package com.cforce.reminder.data

import com.squareup.moshi.Json

data class ApiResponse<T>(
	@Json(name = "status") val status: String,
	@Json(name = "comment") val comment: String?,
	@Json(name = "result") val result: T?
)

data class Contest(
	@Json(name = "id") val id: Long,
	@Json(name = "name") val name: String,
	@Json(name = "type") val type: String?,
	@Json(name = "phase") val phase: String,
	@Json(name = "frozen") val frozen: Boolean,
	@Json(name = "durationSeconds") val durationSeconds: Long,
	@Json(name = "startTimeSeconds") val startTimeSeconds: Long?,
	@Json(name = "relativeTimeSeconds") val relativeTimeSeconds: Long?
) {
	val url: String get() = "https://codeforces.com/contests/${id}"
}
