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

    fun apply(
        context: Context,
        source: String,
        isCopy: Boolean,
        setLock: Boolean,
        setHome: Boolean
    ): Boolean {
        val bitmap = try {
            loadBitmap(context, source, isCopy) ?: return false
        } catch (e: Exception) {
            return false
        }
        val cropped = try {
            centerCrop(context, bitmap)
        } catch (e: Exception) {
            return false
        }

        val wm = WallpaperManager.getInstance(context)
        var flag = 0
        if (setLock) flag = flag or WallpaperManager.FLAG_LOCK
        if (setHome) flag = flag or WallpaperManager.FLAG_SYSTEM
        if (flag == 0) return false

        return try {
            wm.setBitmap(cropped, null, true, flag)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun loadBitmap(context: Context, source: String, isCopy: Boolean): Bitmap? {
        val (reqW, reqH) = screenSize(context)
        return if (isCopy) {
            val f = File(source)
            if (!f.exists()) return null
            decodeSampled(f.path, reqW, reqH)
        } else {
            val uri = Uri.parse(source)
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeStream(input, null, opts)
                    context.contentResolver.openInputStream(uri)?.use { input2 ->
                        val opts2 = BitmapFactory.Options().apply {
                            inJustDecodeBounds = false
                            inSampleSize = calcInSampleSize(opts.outWidth, opts.outHeight, reqW, reqH)
                        }
                        BitmapFactory.decodeStream(input2, null, opts2)
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun decodeSampled(path: String, reqW: Int, reqH: Int): Bitmap? {
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, opts)
        if (opts.outWidth <= 0 || opts.outHeight <= 0) return null
        val opts2 = BitmapFactory.Options().apply {
            inJustDecodeBounds = false
            inSampleSize = calcInSampleSize(opts.outWidth, opts.outHeight, reqW, reqH)
        }
        return BitmapFactory.decodeFile(path, opts2)
    }

    private fun calcInSampleSize(srcW: Int, srcH: Int, reqW: Int, reqH: Int): Int {
        var sample = 1
        if (srcW <= 0 || srcH <= 0) return 1
        while (srcW / (sample * 2) >= reqW && srcH / (sample * 2) >= reqH) {
            sample *= 2
        }
        return sample
    }

    private fun screenSize(context: Context): Pair<Int, Int> {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val b = wm.currentWindowMetrics.bounds
            b.width() to b.height()
        } else {
            val dm = DisplayMetrics()
            @Suppress("DEPRECATION")
            wm.defaultDisplay.getRealMetrics(dm)
            dm.widthPixels to dm.heightPixels
        }
    }

    private fun centerCrop(context: Context, src: Bitmap): Bitmap {
        val (targetW, targetH) = screenSize(context)
        val srcW = src.width
        val srcH = src.height
        if (srcW <= 0 || srcH <= 0) return src

        val scale = maxOf(targetW.toFloat() / srcW, targetH.toFloat() / srcH)
        val scaledW = (srcW * scale).toInt().coerceAtLeast(1)
        val scaledH = (srcH * scale).toInt().coerceAtLeast(1)

        val matrix = Matrix()
        matrix.postScale(scale, scale)
        val scaled = Bitmap.createBitmap(src, 0, 0, srcW, srcH, matrix, true)

        val x = ((scaledW - targetW) / 2).coerceAtLeast(0)
        val y = ((scaledH - targetH) / 2).coerceAtLeast(0)
        val w = targetW.coerceAtMost(scaled.width - x)
        val h = targetH.coerceAtMost(scaled.height - y)

        return Bitmap.createBitmap(scaled, x, y, w, h)
    }
}
