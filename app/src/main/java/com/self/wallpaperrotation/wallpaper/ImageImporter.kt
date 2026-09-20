package com.self.wallpaperrotation.wallpaper
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.self.wallpaperrotation.data.WallpaperItem
import com.self.wallpaperrotation.data.WallpaperRepository
import com.self.wallpaperrotation.data.WallpaperType
import java.io.File
import java.util.UUID
object ImageImporter {
    fun addReference(ctx: Context, uri: Uri): Boolean {
        try { ctx.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
        WallpaperRepository.add(ctx, WallpaperItem(type = WallpaperType.REFERENCE, source = uri.toString()))
        return true
    }
    fun importCopy(ctx: Context, uri: Uri): Boolean {
        return try {
            val d = File(ctx.filesDir, "wallpapers").apply { mkdirs() }
            val dest = File(d, "${UUID.randomUUID()}.jpg")
            ctx.contentResolver.openInputStream(uri)?.use { i -> dest.outputStream().use { i.copyTo(it) } } ?: return false
            WallpaperRepository.add(ctx, WallpaperItem(type = WallpaperType.COPY, source = dest.absolutePath))
            true
        } catch (e: Exception) { false }
    }
}