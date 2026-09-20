package com.self.wallpaperrotation.widget
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.self.wallpaperrotation.R
import com.self.wallpaperrotation.rotation.RotationReceiver
class NextWallpaperWidget : AppWidgetProvider() {
    override fun onUpdate(ctx: Context, mgr: AppWidgetManager, ids: IntArray) {
        for (id in ids) {
            val v = RemoteViews(ctx.packageName, R.layout.widget_next)
            val pi = PendingIntent.getBroadcast(ctx, 0,
                Intent(ctx, RotationReceiver::class.java).apply { action = RotationReceiver.ACTION_NEXT },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            v.setOnClickPendingIntent(R.id.widget_root, pi)
            mgr.updateAppWidget(id, v)
        }
    }
}