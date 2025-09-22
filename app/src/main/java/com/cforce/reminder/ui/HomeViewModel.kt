package com.cforce.reminder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cforce.reminder.data.Contest
import com.cforce.reminder.data.ContestRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class HomeViewModel(private val repository: ContestRepository = ContestRepository()) : ViewModel() {
	private val _contests = MutableStateFlow<List<Contest>>(emptyList())
	val contests: StateFlow<List<Contest>> = _contests

	private val _now = MutableStateFlow(Clock.System.now().epochSeconds)
	val now: StateFlow<Long> = _now

	private var ticker: Job? = null

	init {
		refresh()
		startTicker()
	}

	fun refresh() {
		viewModelScope.launch {
			_contests.value = repository.getUpcomingContests()
		}
	}

	private fun startTicker() {
		ticker?.cancel()
		ticker = viewModelScope.launch {
			while (true) {
				_now.value = Clock.System.now().epochSeconds
				delay(1000)
			}
		}
	}
}
