package com.self.wallpaperrotation.rotation
object RotationLock {
    @Volatile private var locked = false
    @Synchronized fun tryLock(): Boolean {
        if (locked) return false
        locked = true; return true
    }
    @Synchronized fun unlock() { locked = false }
    @Synchronized fun isLocked(): Boolean = locked
}