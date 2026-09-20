package com.self.wallpaperrotation.data

import java.util.UUID

enum class WallpaperType {
    REFERENCE,
    COPY
}

data class WallpaperItem(
    val id: String = UUID.randomUUID().toString(),
    val type: WallpaperType,
    val source: String,
    val addedTime: Long = System.currentTimeMillis(),
    val enabled: Boolean = true
)
