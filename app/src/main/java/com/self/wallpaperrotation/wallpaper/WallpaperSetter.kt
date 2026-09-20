package com.self.wallpaperrotation.wallpaper
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import java.io.File
object WallpaperSetter {
    fun apply(ctx: Context, source: String, isCopy: Boolean, setLock: Boolean, setHome: Boolean, cropOffset: Float = 0.5f): Boolean {
        val bmp = try { load(ctx, source, isCopy) ?: return false } catch (e: Exception) { return false }
        val cr = try { crop(ctx, bmp, cropOffset) } catch (e: Exception) { return false }
        val wm = WallpaperManager.getInstance(ctx)
        var flag = 0
        if (setLock) flag = flag or WallpaperManager.FLAG_LOCK
        if (setHome) flag = flag or WallpaperManager.FLAG_SYSTEM
        if (flag == 0) return false
        return try { wm.setBitmap(cr, null, true, flag); true } catch (e: Exception) { false }
    }
    private fun load(ctx: Context, source: String, isCopy: Boolean): Bitmap? {
        val (w, h) = screen(ctx)
        return if (isCopy) {
            val f = File(source); if (!f.exists()) return null
            decode(f.path, w, h)
        } else try {
            val uri = Uri.parse(source)
            ctx.contentResolver.openInputStream(uri)?.use { i ->
                val o = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(i, null, o)
                ctx.contentResolver.openInputStream(uri)?.use { i2 ->
                    val o2 = BitmapFactory.Options().apply {
                        inJustDecodeBounds = false
                        inSampleSize = calc(o.outWidth, o.outHeight, w, h)
                    }
                    BitmapFactory.decodeStream(i2, null, o2)
                }
            }
        } catch (e: Exception) { null }
    }
    private fun decode(p: String, w: Int, h: Int): Bitmap? {
        val o = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(p, o)
        if (o.outWidth <= 0 || o.outHeight <= 0) return null
        val o2 = BitmapFactory.Options().apply {
            inJustDecodeBounds = false
            inSampleSize = calc(o.outWidth, o.outHeight, w, h)
        }
        return BitmapFactory.decodeFile(p, o2)
    }
    private fun calc(w: Int, h: Int, rw: Int, rh: Int): Int {
        var s = 1; if (w <= 0 || h <= 0) return 1
        while (w / (s * 2) >= rw && h / (s * 2) >= rh) s *= 2
        return s
    }
    private fun screen(c: Context): Pair<Int, Int> {
        val wm = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val b = wm.currentWindowMetrics.bounds; b.width() to b.height()
        } else {
            val dm = DisplayMetrics()
            @Suppress("DEPRECATION") wm.defaultDisplay.getRealMetrics(dm)
            dm.widthPixels to dm.heightPixels
        }
    }
    private fun crop(ctx: Context, src: Bitmap, off: Float): Bitmap {
        val (tw, th) = screen(ctx)
        val sw = src.width; val sh = src.height
        if (sw <= 0 || sh <= 0) return src
        val sc = maxOf(tw.toFloat() / sw, th.toFloat() / sh)
        val scw = (sw * sc).toInt().coerceAtLeast(1)
        val sch = (sh * sc).toInt().coerceAtLeast(1)
        val m = Matrix(); m.postScale(sc, sc)
        val s = Bitmap.createBitmap(src, 0, 0, sw, sh, m, true)
        val ew = (scw - tw).coerceAtLeast(0); val eh = (sch - th).coerceAtLeast(0)
        val x = (ew / 2).coerceIn(0, ew)
        val y = (eh * off.coerceIn(0f, 1f)).toInt().coerceIn(0, eh)
        val w = tw.coerceAtMost(s.width - x); val h = th.coerceAtMost(s.height - y)
        return Bitmap.createBitmap(s, x, y, w, h)
    }
}