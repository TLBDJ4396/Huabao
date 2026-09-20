package com.self.wallpaperrotation.rotation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.self.wallpaperrotation.data.AppSettings
import com.self.wallpaperrotation.data.WallpaperRepository
import com.self.wallpaperrotation.data.WallpaperType
import com.self.wallpaperrotation.notification.NotificationHelper
import com.self.wallpaperrotation.wallpaper.WallpaperSetter

class RotationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TICK = "com.self.wallpaperrotation.TICK"
        const val ACTION_NEXT = "com.self.wallpaperrotation.NEXT"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val settings = AppSettings(context)

        when (intent.action) {
            ACTION_TICK -> {
                if (!settings.rotationEnabled) return
                val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                if (pm.isInteractive) {
                    nextWallpaper(context)
                }
                RotationScheduler.schedule(context)
            }
            ACTION_NEXT -> {
                nextWallpaper(context)
                if (settings.rotationEnabled) {
                    RotationScheduler.schedule(context)
                }
            }
        }
    }

    private fun nextWallpaper(context: Context) {
        val settings = AppSettings(context)
        val list = WallpaperRepository.load(context).filter { it.enabled }
        if (list.isEmpty()) return

        var index = settings.cursorIndex % list.size
        if (index < 0) index = 0

        for (i in list.indices) {
            val item = list[index]
            val ok = WallpaperSetter.apply(
                context,
                item.source,
                item.type == WallpaperType.COPY,
                settings.setLockScreen,
                settings.setHomeScreen
            )
            if (ok) {
                settings.cursorIndex = (index + 1) % list.size
                NotificationHelper.update(context)
                return
            }
            index = (index + 1) % list.size
        }
    }
}
