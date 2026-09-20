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
    private const val CHANNEL_ID = "wallpaper_channel"
    private const val NOTIFICATION_ID = 1001

    fun show(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID, "澹佺焊杞崲", NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "澹佺焊杞崲鐘舵€佷笌蹇嵎鎿嶄綔"
                setShowBadge(false)
            }
            nm.createNotificationChannel(ch)
        }

        val openIntent = Intent(context, MainActivity::class.java)
        val openPi = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = Intent(context, RotationReceiver::class.java).apply {
            action = RotationReceiver.ACTION_NEXT
        }
        val nextPi = PendingIntent.getBroadcast(
            context, 1, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val settings = AppSettings(context)
        val text = if (settings.rotationEnabled) {
            "姣?${settings.intervalMinutes} 鍒嗛挓妫€鏌ヤ竴娆?
        } else {
            "宸叉殏鍋?
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_gallery)
            .setContentTitle("鐢绘姤")
            .setContentText(text)
            .setContentIntent(openPi)
            .addAction(0, "涓嬩竴寮?, nextPi)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        try {
            nm.notify(NOTIFICATION_ID, notification)
        } catch (_: Exception) {
        }
    }

    fun update(context: Context) = show(context)

    fun cancel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_ID)
    }
}
