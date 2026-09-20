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

    fun addReference(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: Exception) {
            true
        }.let {
            val item = WallpaperItem(
                type = WallpaperType.REFERENCE,
                source = uri.toString()
            )
            WallpaperRepository.add(context, item)
            true
        }
    }

    fun importCopy(context: Context, uri: Uri): Boolean {
        return try {
            val dir = File(context.filesDir, "wallpapers").apply { mkdirs() }
            val dest = File(dir, "${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                dest.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return false

            val item = WallpaperItem(
                type = WallpaperType.COPY,
                source = dest.absolutePath
            )
            WallpaperRepository.add(context, item)
            true
        } catch (e: Exception) {
            false
        }
    }
}
