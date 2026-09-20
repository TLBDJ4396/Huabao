package com.self.wallpaperrotation.notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.self.wallpaperrotation.MainActivity
import com.self.wallpaperrotation.data.AppSettings
import com.self.wallpaperrotation.rotation.RotationReceiver
object NotificationHelper {
    private const val CH = "wallpaper_channel"
    private const val NID = 1001
    fun show(c: Context) {
        val nm = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(CH, "\u58c1\u7eb8\u8f6e\u6362", NotificationManager.IMPORTANCE_LOW).apply {
                description = "\u58c1\u7eb8\u8f6e\u6362\u72b6\u6001\u4e0e\u5feb\u6377\u64cd\u4f5c"
                setShowBadge(false)
            }
            nm.createNotificationChannel(ch)
        }
        val op = PendingIntent.getActivity(c, 0, Intent(c, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val np = PendingIntent.getBroadcast(c, 1,
            Intent(c, RotationReceiver::class.java).apply { action = RotationReceiver.ACTION_NEXT },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val s = AppSettings(c)
        val txt = if (s.rotationEnabled) "\u6bcf ${s.intervalMinutes} \u5206\u949f\u68c0\u67e5\u4e00\u6b21" else "\u5df2\u6682\u505c"
        val n = NotificationCompat.Builder(c, CH)
            .setSmallIcon(android.R.drawable.ic_menu_gallery)
            .setContentTitle("\u753b\u62a5").setContentText(txt).setContentIntent(op)
            .addAction(0, "\u4e0b\u4e00\u5f20", np).setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW).build()
        try { nm.notify(NID, n) } catch (_: Exception) {}
    }
    fun update(c: Context) = show(c)
    fun cancel(c: Context) {
        val nm = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NID)
    }
}