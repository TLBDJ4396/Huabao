package com.self.wallpaperrotation.rotation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.self.wallpaperrotation.data.AppSettings

object RotationScheduler {

    private const val REQ_CODE = 1001

    fun schedule(context: Context) {
        val settings = AppSettings(context)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = pendingIntent(context)
        val intervalMs = settings.intervalMinutes * 60_000L
        val triggerAt = System.currentTimeMillis() + intervalMs
        try {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        } catch (_: Exception) {
        }
    }

    fun cancel(context: Context) {
        try {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.cancel(pendingIntent(context))
        } catch (_: Exception) {
        }
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, RotationReceiver::class.java).apply {
            action = RotationReceiver.ACTION_TICK
        }
        return PendingIntent.getBroadcast(
            context, REQ_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
