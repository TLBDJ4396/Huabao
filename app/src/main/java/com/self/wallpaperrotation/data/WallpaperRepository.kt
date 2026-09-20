package com.self.wallpaperrotation.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object WallpaperRepository {
    private const val FILE_NAME = "wallpapers.json"
    private val gson = Gson()

    private fun file(context: Context) = File(context.filesDir, FILE_NAME)

    fun load(context: Context): MutableList<WallpaperItem> {
        val f = file(context)
        if (!f.exists()) return mutableListOf()
        return try {
            val type = object : TypeToken<MutableList<WallpaperItem>>() {}.type
            gson.fromJson<MutableList<WallpaperItem>>(f.readText(), type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    fun save(context: Context, list: List<WallpaperItem>) {
        try {
            file(context).writeText(gson.toJson(list))
        } catch (_: Exception) {
        }
    }

    fun add(context: Context, item: WallpaperItem) {
        val list = load(context)
        list.add(item)
        save(context, list)
    }

    fun remove(context: Context, id: String) {
        val list = load(context).filterNot { it.id == id }.toMutableList()
        save(context, list)
    }

    fun clear(context: Context) {
        save(context, emptyList())
    }
}
