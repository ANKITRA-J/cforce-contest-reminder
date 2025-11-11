package com.cforce.reminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cforce.reminder.notify.WorkScheduler
import com.cforce.reminder.ui.AppRoot
import android.Manifest
import android.os.Build

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		WorkScheduler.schedulePeriodic(this)

		// Request notification permission on Android 13+
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			val requestPermissionLauncher = registerForActivityResult(
				ActivityResultContracts.RequestPermission()
			) { _ -> }
			requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
		}

		setContent {
			CFTheme {
				Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
					AppRoot()
				}
			}
		}
	}
}

@Composable
fun CFTheme(content: @Composable () -> Unit) {
	MaterialTheme {
		content()
	}
}
