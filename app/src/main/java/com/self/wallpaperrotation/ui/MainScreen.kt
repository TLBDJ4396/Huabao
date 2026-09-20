package com.self.wallpaperrotation.ui
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.self.wallpaperrotation.MainActivity
import com.self.wallpaperrotation.data.WallpaperItem

@Composable
fun MainScreen(a: MainActivity) {
    var tab by remember { mutableIntStateOf(0) }
    var rf by remember { mutableIntStateOf(0) }
    var pv by remember { mutableStateOf<WallpaperItem?>(null) }
    var st by remember { mutableStateOf(false) }
    pv?.let { PreviewScreen(a, it, onBack = { pv = null; rf++ }, onChanged = { rf++ }); return }
    if (st) { TagsScreen(a, onBack = { st = false; rf++ }); return }
    Scaffold(bottomBar = {
        NavigationBar {
            NavigationBarItem(selected = tab == 0, onClick = { tab = 0 },
                icon = { Icon(Icons.Default.Home, contentDescription = "home") },
                label = { Text("\u4e3b\u9875") })
            NavigationBarItem(selected = tab == 1, onClick = { tab = 1; rf++ },
                icon = { Icon(Icons.Default.List, contentDescription = "list") },
                label = { Text("\u56fe\u5e93") })
            NavigationBarItem(selected = tab == 2, onClick = { tab = 2 },
                icon = { Icon(Icons.Default.Settings, contentDescription = "settings") },
                label = { Text("\u8bbe\u7f6e") })
        }
    }) { p ->
        Box(Modifier.padding(p).fillMaxSize()) {
            when (tab) {
                0 -> HomeScreen(a, rf) { tab = 1; rf++ }
                1 -> GalleryScreen(a, rf, onOpenPreview = { pv = it }, onChanged = { rf++ })
                2 -> SettingsScreen(a, onChanged = { rf++ }, onOpenTags = { st = true })
            }
        }
    }
}