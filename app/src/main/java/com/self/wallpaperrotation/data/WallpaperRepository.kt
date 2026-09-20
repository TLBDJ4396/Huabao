package com.self.wallpaperrotation.data
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
object WallpaperRepository {
    private const val FN = "wallpapers.json"
    private val gson = Gson()
    private fun f(c: Context) = File(c.filesDir, FN)
    fun load(c: Context): MutableList<WallpaperItem> {
        val file = f(c)
        if (!file.exists()) return mutableListOf()
        return try {
            val t = object : TypeToken<MutableList<WallpaperItem>>() {}.type
            gson.fromJson<MutableList<WallpaperItem>>(file.readText(), t) ?: mutableListOf()
        } catch (e: Exception) { mutableListOf() }
    }
    fun save(c: Context, l: List<WallpaperItem>) { try { f(c).writeText(gson.toJson(l)) } catch (_: Exception) {} }
    fun add(c: Context, i: WallpaperItem) { val l = load(c); l.add(i); save(c, l) }
    fun remove(c: Context, id: String) { val l = load(c).filterNot { it.id == id }.toMutableList(); save(c, l) }
    fun update(c: Context, i: WallpaperItem) {
        val l = load(c); val idx = l.indexOfFirst { it.id == i.id }
        if (idx >= 0) { l[idx] = i; save(c, l) }
    }
    fun clear(c: Context) { save(c, emptyList()) }
    fun setCursorIndex(c: Context, id: String) {
        val l = load(c).filter { it.enabled }
        val idx = l.indexOfFirst { it.id == id }
        if (idx >= 0) { AppSettings(c).cursorIndex = idx }
    }
    fun exportBackup(c: Context): String {
        val d = BackupData(load(c), TagRepository.load(c), System.currentTimeMillis(), 2)
        return gson.toJson(d)
    }
    fun importBackup(c: Context, json: String): Boolean {
        return try {
            val d: BackupData = gson.fromJson(json, BackupData::class.java)
            if (d.wallpapers != null) save(c, d.wallpapers)
            if (d.tags != null) TagRepository.save(c, d.tags)
            true
        } catch (e: Exception) { false }
    }
    data class BackupData(
        val wallpapers: List<WallpaperItem>?,
        val tags: List<Tag>?,
        val exportTime: Long,
        val version: Int
    )
}