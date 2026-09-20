package com.self.wallpaperrotation.data
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
object TagRepository {
    private const val FN = "tags.json"
    private val gson = Gson()
    private fun f(c: Context) = File(c.filesDir, FN)
    fun load(c: Context): MutableList<Tag> {
        val file = f(c)
        if (!file.exists()) return mutableListOf()
        return try {
            val t = object : TypeToken<MutableList<Tag>>() {}.type
            gson.fromJson<MutableList<Tag>>(file.readText(), t) ?: mutableListOf()
        } catch (e: Exception) { mutableListOf() }
    }
    fun save(c: Context, l: List<Tag>) { try { f(c).writeText(gson.toJson(l)) } catch (_: Exception) {} }
    fun add(c: Context, t: Tag) { val l = load(c); l.add(t); save(c, l) }
    fun remove(c: Context, id: String) { val l = load(c).filterNot { it.id == id }.toMutableList(); save(c, l) }
}