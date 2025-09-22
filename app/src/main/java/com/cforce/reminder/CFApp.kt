package com.cforce.reminder

import android.app.Application

class CFApp : Application() {
	override fun onCreate() {
		super.onCreate()
		Notifications.createChannels(this)
	}
}
