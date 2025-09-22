package com.cforce.reminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cforce.reminder.notify.WorkScheduler
import com.cforce.reminder.ui.AppRoot

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		WorkScheduler.schedulePeriodic(this)
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
	MaterialTheme(colorScheme = darkColorScheme) {
		content()
	}
}

private val darkColorScheme = androidx.compose.material3.darkColorScheme()
