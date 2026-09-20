package com.self.wallpaperrotation.rotation
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.self.wallpaperrotation.data.AppSettings
import com.self.wallpaperrotation.data.TagRepository
import com.self.wallpaperrotation.data.WallpaperItem
import com.self.wallpaperrotation.data.WallpaperRepository
import com.self.wallpaperrotation.data.WallpaperType
import com.self.wallpaperrotation.notification.NotificationHelper
import com.self.wallpaperrotation.wallpaper.WallpaperSetter
class RotationReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_TICK = "com.self.wallpaperrotation.TICK"
        const val ACTION_NEXT = "com.self.wallpaperrotation.NEXT"
    }
    override fun onReceive(ctx: Context, intent: Intent) {
        val s = AppSettings(ctx)
        when (intent.action) {
            ACTION_TICK -> {
                if (!s.rotationEnabled) return
                val pm = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
                if (pm.isInteractive) perform(ctx)
                RotationScheduler.schedule(ctx)
            }
            ACTION_NEXT -> {
                perform(ctx)
                if (s.rotationEnabled) RotationScheduler.schedule(ctx)
            }
        }
    }
    private fun perform(ctx: Context) {
        if (!RotationLock.tryLock()) return
        try { next(ctx) } finally { RotationLock.unlock() }
    }
    private fun next(ctx: Context) {
        val s = AppSettings(ctx)
        val all = WallpaperRepository.load(ctx).filter { it.enabled }
        if (all.isEmpty()) return
        val filtered = filterByTags(ctx, all)
        val list = if (filtered.isEmpty()) all else filtered
        if (list.isEmpty()) return
        var idx = s.cursorIndex % list.size
        if (idx < 0) idx = 0
        var att = 0
        while (att < 3 && att < list.size) {
            val it = list[idx]
            if (WallpaperSetter.apply(ctx, it.source, it.type == WallpaperType.COPY, s.setLockScreen, s.setHomeScreen, it.cropOffset)) {
                s.cursorIndex = (idx + 1) % list.size
                NotificationHelper.update(ctx)
                return
            }
            att++; idx = (idx + 1) % list.size
        }
    }
    private fun filterByTags(c: Context, l: List<WallpaperItem>): List<WallpaperItem> {
        val s = AppSettings(c)
        val tags = TagRepository.load(c)
        val pid = s.activeHomeTagId ?: s.activeLockTagId ?: return l
        val t = tags.find { it.id == pid } ?: return l
        if (!t.enabled) return l
        val f = l.filter { it.tags.contains(pid) }
        return if (f.isEmpty()) l else f
    }
}