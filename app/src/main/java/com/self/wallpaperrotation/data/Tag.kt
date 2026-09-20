package com.self.wallpaperrotation.data
import java.util.UUID
enum class TagScope { HOME, LOCK, BOTH }
data class Tag(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val scope: TagScope = TagScope.BOTH,
    val enabled: Boolean = true
)