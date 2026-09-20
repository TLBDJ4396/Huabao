package com.self.wallpaperrotation.rotation
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.self.wallpaperrotation.data.AppSettings
import com.self.wallpaperrotation.notification.NotificationHelper
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) {
        if (i.action == Intent.ACTION_BOOT_COMPLETED) {
            if (AppSettings(c).rotationEnabled) RotationScheduler.schedule(c)
            NotificationHelper.show(c)
        }
    }
}