package com.cforce.reminder.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cforce.reminder.data.Contest
import com.cforce.reminder.notify.WorkScheduler
import com.cforce.reminder.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@Composable
fun AppRoot() {
	val nav = rememberNavController()
	NavHost(navController = nav, startDestination = "home") {
		composable("home") { HomeScreen(nav) }
		composable("settings") { SettingsScreen(nav) }
	}
}

@Composable
fun HomeScreen(nav: NavHostController, vm: HomeViewModel = viewModel()) {
	val contests by vm.contests.collectAsState()
	val now by vm.now.collectAsState()
	val ctx = LocalContext.current
	val settings = remember { SettingsRepository(ctx) }.flow.collectAsState(initial = null).value
	TopBarScaffold(title = "Upcoming Contests", onSettings = { nav.navigate("settings") }, onCheck = { WorkScheduler.triggerOnce(ctx) }) {
		LazyColumn(Modifier.fillMaxSize()) {
			items(contests) { c -> ContestRow(c, now, settings?.timezoneId ?: TimeZone.getDefault().id) }
		}
	}
}

@Composable
private fun ContestRow(c: Contest, nowEpoch: Long, timezoneId: String) {
	val start = (c.startTimeSeconds ?: 0L) * 1000
	val sdf = remember { SimpleDateFormat("EEE, dd MMM yyyy HH:mm") }
	sdf.timeZone = TimeZone.getTimeZone(timezoneId)
	val timeLeftSec = (c.startTimeSeconds ?: 0L) - (nowEpoch)
	val h = timeLeftSec / 3600
	val m = (timeLeftSec % 3600) / 60
	val s = timeLeftSec % 60
	Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
		Text(c.name, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
		Spacer(Modifier.height(4.dp))
		Text("Starts: ${sdf.format(Date(start))}", style = MaterialTheme.typography.bodySmall)
		Spacer(Modifier.height(2.dp))
		Text("Time left: ${h}h ${m}m ${s}s", style = MaterialTheme.typography.bodyMedium)
	}
}

@Composable
fun SettingsScreen(nav: NavHostController) {
	val ctx = LocalContext.current
	val repo = remember { SettingsRepository(ctx) }
	val settings by repo.flow.collectAsState(initial = null)

	TopBarScaffold(title = "Settings") {
		if (settings == null) return@TopBarScaffold
		Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
			Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
				Text("Notifications")
				Switch(checked = settings!!.notificationsEnabled, onCheckedChange = { checked ->
					launchIo { repo.setNotificationsEnabled(checked); toast(ctx, if (checked) "Enabled" else "Disabled") }
				})
			}

			Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
				Text("Lead time (minutes)")
				Row { Button(onClick = { launchIo { repo.setLeadMinutes((settings!!.leadMinutes - 5).coerceAtLeast(5)) } }) { Text("-") }
					Spacer(Modifier.width(8.dp))
					Text(settings!!.leadMinutes.toString(), modifier = Modifier.padding(top = 12.dp))
					Spacer(Modifier.width(8.dp))
					Button(onClick = { launchIo { repo.setLeadMinutes((settings!!.leadMinutes + 5).coerceAtMost(120)) } }) { Text("+") }
				}
			}

			Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
				Text("Timezone")
				Button(onClick = {
					launchIo {
						val zones = listOf("Asia/Kolkata", "Europe/London", java.util.TimeZone.getDefault().id)
						val idx = zones.indexOf(settings!!.timezoneId).let { if (it == -1) 0 else it }
						val next = zones[(idx + 1) % zones.size]
						repo.setTimezone(next)
					}
				}) { Text(settings!!.timezoneId) }
			}

			Button(onClick = { WorkScheduler.schedulePeriodic(ctx); toast(ctx, "Background checks scheduled") }) {
				Text("Schedule background checks")
			}
		}
	}
}

@Composable
private fun TopBarScaffold(
	title: String,
	onSettings: (() -> Unit)? = null,
	onCheck: (() -> Unit)? = null,
	content: @Composable () -> Unit
) {
	Column(modifier = Modifier.fillMaxSize()) {
		Row(
			modifier = Modifier.fillMaxWidth().padding(16.dp)
		) {
			Text(title)
			Spacer(modifier = Modifier.width(16.dp))
			if (onCheck != null) Button(onClick = onCheck) { Text("Check Now") }
			if (onSettings != null) Button(onClick = onSettings) { Text("Settings") }
		}
		content()
	}
}

private fun launchIo(block: suspend () -> Unit) {
	CoroutineScope(Dispatchers.IO).launch { block() }
}

private fun toast(ctx: Context, msg: String) {
	Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
}
