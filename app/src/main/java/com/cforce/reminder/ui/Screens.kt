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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import kotlinx.coroutines.withContext
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
			items(contests) { c -> ContestRow(c, now, settings?.timezoneId ?: TimeZone.getDefault().id, leadMinutes = settings?.leadMinutes ?: 30) }
		}
	}
}

@Composable
private fun ContestRow(c: Contest, nowEpoch: Long, timezoneId: String, leadMinutes: Int) {
	val start = (c.startTimeSeconds ?: 0L) * 1000
	val sdf = remember { SimpleDateFormat("EEE, dd MMM yyyy HH:mm") }
	sdf.timeZone = TimeZone.getTimeZone(timezoneId)
	val timeLeftSec = (c.startTimeSeconds ?: 0L) - (nowEpoch)
	val h = timeLeftSec / 3600
	val m = (timeLeftSec % 3600) / 60
	val s = timeLeftSec % 60
	
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(
				text = c.name,
				style = MaterialTheme.typography.titleMedium,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis
			)
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				text = "Starts: ${sdf.format(Date(start))}",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(modifier = Modifier.height(4.dp))
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = "Time left: ",
					style = MaterialTheme.typography.bodyMedium
				)
				Text(
					text = "${h}h ${m}m ${s}s",
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.primary
				)
			}
			Spacer(modifier = Modifier.height(12.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text("Alarm: ${leadMinutes} min before")
				val ctx = LocalContext.current
				Button(onClick = {
					com.cforce.reminder.notify.AlarmScheduler.scheduleExact(ctx, c, leadMinutes)
					Toast.makeText(ctx, "Alarm set for ${leadMinutes} min before", Toast.LENGTH_SHORT).show()
				}) {
					Text("Set Alarm")
				}
			}
		}
	}
}

@Composable
fun SettingsScreen(nav: NavHostController) {
	val ctx = LocalContext.current
	val repo = remember { SettingsRepository(ctx) }
	val settings by repo.flow.collectAsState(initial = null)

	TopBarScaffold(title = "Settings") {
		if (settings == null) return@TopBarScaffold
		LazyColumn(
			modifier = Modifier.fillMaxSize().padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(24.dp)
		) {
			item {
				// Notifications Section
				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
				) {
					Column(modifier = Modifier.padding(16.dp)) {
						Text("Notifications", style = MaterialTheme.typography.titleMedium)
						Spacer(modifier = Modifier.height(8.dp))
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Text("Enable notifications")
							Switch(
								checked = settings!!.notificationsEnabled,
								onCheckedChange = { checked ->
									CoroutineScope(Dispatchers.IO).launch {
										repo.setNotificationsEnabled(checked)
										withContext(Dispatchers.Main) {
											Toast.makeText(ctx, if (checked) "Notifications enabled" else "Notifications disabled", Toast.LENGTH_SHORT).show()
										}
									}
								}
							)
						}
					}
				}
			}

			item {
				// Lead Time Section
				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
				) {
					Column(modifier = Modifier.padding(16.dp)) {
						Text("Reminder Timing", style = MaterialTheme.typography.titleMedium)
						Spacer(modifier = Modifier.height(8.dp))
						Text("Get notified before contest starts", style = MaterialTheme.typography.bodyMedium)
						Spacer(modifier = Modifier.height(12.dp))
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Text("Lead time: ${settings!!.leadMinutes} minutes")
							Row(verticalAlignment = Alignment.CenterVertically) {
								Button(
									onClick = {
										CoroutineScope(Dispatchers.IO).launch {
											repo.setLeadMinutes((settings!!.leadMinutes - 5).coerceAtLeast(5))
										}
									},
									enabled = settings!!.leadMinutes > 5
								) { Text("-") }
								Spacer(modifier = Modifier.width(8.dp))
								Button(
									onClick = {
										CoroutineScope(Dispatchers.IO).launch {
											repo.setLeadMinutes((settings!!.leadMinutes + 5).coerceAtMost(120))
										}
									},
									enabled = settings!!.leadMinutes < 120
								) { Text("+") }
							}
						}
					}
				}
			}

			item {
				// Timezone Section
				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
				) {
					Column(modifier = Modifier.padding(16.dp)) {
						Text("Timezone", style = MaterialTheme.typography.titleMedium)
						Spacer(modifier = Modifier.height(8.dp))
						Text("Current: ${getTimezoneDisplayName(settings!!.timezoneId)}", style = MaterialTheme.typography.bodyMedium)
						Spacer(modifier = Modifier.height(12.dp))
						Button(
							onClick = {
								CoroutineScope(Dispatchers.IO).launch {
									val zones = listOf(
										"America/New_York", "America/Los_Angeles", "America/Chicago", "America/Denver",
										"Europe/London", "Europe/Paris", "Europe/Berlin", "Europe/Moscow",
										"Asia/Tokyo", "Asia/Shanghai", "Asia/Kolkata", "Australia/Sydney"
									)
									val idx = zones.indexOf(settings!!.timezoneId).let { if (it == -1) 0 else it }
									val next = zones[(idx + 1) % zones.size]
									repo.setTimezone(next)
								}
							},
							modifier = Modifier.fillMaxWidth()
						) {
							Text("Switch to: ${getTimezoneDisplayName(getNextTimezone(settings!!.timezoneId))}")
						}
					}
				}
			}

			item {
				// Actions Section
				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
				) {
					Column(modifier = Modifier.padding(16.dp)) {
						Text("Actions", style = MaterialTheme.typography.titleMedium)
						Spacer(modifier = Modifier.height(12.dp))
						Button(
							onClick = {
								WorkScheduler.schedulePeriodic(ctx)
								Toast.makeText(ctx, "Background checks scheduled", Toast.LENGTH_SHORT).show()
							},
							modifier = Modifier.fillMaxWidth()
						) {
							Text("Schedule Background Checks")
						}
					}
				}
			}

			item {
				// Developer Credit
				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
				) {
					Column(
						modifier = Modifier.padding(16.dp),
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Text("Made with ❤️ by Ankit Raj", style = MaterialTheme.typography.bodyMedium)
						Spacer(modifier = Modifier.height(8.dp))
						Button(
							onClick = {
								val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
								intent.data = android.net.Uri.parse("https://github.com/ANKITRA-J")
								ctx.startActivity(intent)
							}
						) {
							Text("View GitHub Profile")
						}
					}
				}
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
		Card(
			modifier = Modifier.fillMaxWidth(),
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = title,
					style = MaterialTheme.typography.headlineSmall,
					modifier = Modifier.weight(1f)
				)
				if (onCheck != null) {
					Button(onClick = onCheck) {
						Text("Refresh")
					}
				}
				Spacer(modifier = Modifier.width(8.dp))
				if (onSettings != null) {
					Button(onClick = onSettings) {
						Text("Settings")
					}
				}
			}
		}
		content()
	}
}

private fun getTimezoneDisplayName(timezoneId: String): String {
	return when (timezoneId) {
		"America/New_York" -> "New York (EST/EDT)"
		"America/Los_Angeles" -> "Los Angeles (PST/PDT)"
		"America/Chicago" -> "Chicago (CST/CDT)"
		"America/Denver" -> "Denver (MST/MDT)"
		"Europe/London" -> "London (GMT/BST)"
		"Europe/Paris" -> "Paris (CET/CEST)"
		"Europe/Berlin" -> "Berlin (CET/CEST)"
		"Europe/Moscow" -> "Moscow (MSK)"
		"Asia/Tokyo" -> "Tokyo (JST)"
		"Asia/Shanghai" -> "Shanghai (CST)"
		"Asia/Kolkata" -> "Mumbai/Delhi (IST)"
		"Australia/Sydney" -> "Sydney (AEST/AEDT)"
		else -> timezoneId
	}
}

private fun getNextTimezone(currentTimezone: String): String {
	val zones = listOf(
		"America/New_York", "America/Los_Angeles", "America/Chicago", "America/Denver",
		"Europe/London", "Europe/Paris", "Europe/Berlin", "Europe/Moscow",
		"Asia/Tokyo", "Asia/Shanghai", "Asia/Kolkata", "Australia/Sydney"
	)
	val idx = zones.indexOf(currentTimezone).let { if (it == -1) 0 else it }
	return zones[(idx + 1) % zones.size]
}

private fun launchIo(block: suspend () -> Unit) {
	CoroutineScope(Dispatchers.IO).launch { block() }
}

private fun toast(ctx: Context, msg: String) {
	Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
}
