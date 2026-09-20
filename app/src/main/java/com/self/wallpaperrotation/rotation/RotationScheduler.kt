package com.self.wallpaperrotation.rotation
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.self.wallpaperrotation.data.AppSettings
object RotationScheduler {
    private const val RC = 1001
    fun schedule(c: Context) {
        val s = AppSettings(c)
        val am = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = pi(c)
        try { am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + s.intervalMinutes * 60_000L, pi) } catch (_: Exception) {}
    }
    fun cancel(c: Context) {
        try {
            val am = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.cancel(pi(c))
        } catch (_: Exception) {}
    }
    private fun pi(c: Context): PendingIntent {
        val i = Intent(c, RotationReceiver::class.java).apply { action = RotationReceiver.ACTION_TICK }
        return PendingIntent.getBroadcast(c, RC, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
}