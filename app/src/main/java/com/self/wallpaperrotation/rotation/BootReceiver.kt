package com.self.wallpaperrotation.rotation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.self.wallpaperrotation.data.AppSettings
import com.self.wallpaperrotation.notification.NotificationHelper

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val settings = AppSettings(context)
            if (settings.rotationEnabled) {
                RotationScheduler.schedule(context)
            }
            NotificationHelper.show(context)
        }
    }
}
