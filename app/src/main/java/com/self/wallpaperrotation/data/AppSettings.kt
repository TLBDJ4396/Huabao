package com.self.wallpaperrotation.data
import android.content.Context
class AppSettings(c: Context) {
    private val sp = c.getSharedPreferences("settings", Context.MODE_PRIVATE)
    var rotationEnabled: Boolean
        get() = sp.getBoolean("rotation_enabled", true)
        set(v) = sp.edit().putBoolean("rotation_enabled", v).apply()
    var intervalMinutes: Int
        get() = sp.getInt("interval_minutes", 15)
        set(v) = sp.edit().putInt("interval_minutes", v).apply()
    var setLockScreen: Boolean
        get() = sp.getBoolean("set_lock", true)
        set(v) = sp.edit().putBoolean("set_lock", v).apply()
    var setHomeScreen: Boolean
        get() = sp.getBoolean("set_home", true)
        set(v) = sp.edit().putBoolean("set_home", v).apply()
    var cursorIndex: Int
        get() = sp.getInt("cursor_index", 0)
        set(v) = sp.edit().putInt("cursor_index", v).apply()
    var importAsCopy: Boolean
        get() = sp.getBoolean("import_as_copy", true)
        set(v) = sp.edit().putBoolean("import_as_copy", v).apply()
    var activeHomeTagId: String?
        get() = sp.getString("active_home_tag", null)
        set(v) = sp.edit().putString("active_home_tag", v).apply()
    var activeLockTagId: String?
        get() = sp.getString("active_lock_tag", null)
        set(v) = sp.edit().putString("active_lock_tag", v).apply()
}