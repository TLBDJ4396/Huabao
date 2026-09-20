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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.self.wallpaperrotation.MainActivity

@Composable
fun MainScreen(activity: MainActivity) {
    var tab by remember { mutableIntStateOf(0) }
    var refresh by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "涓婚〉") },
                    label = { Text("涓婚〉") }
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1; refresh++ },
                    icon = { Icon(Icons.Default.List, contentDescription = "鍥惧簱") },
                    label = { Text("鍥惧簱") }
                )
                NavigationBarItem(
                    selected = tab == 2,
                    onClick = { tab = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "璁剧疆") },
                    label = { Text("璁剧疆") }
                )
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (tab) {
                0 -> HomeScreen(activity, refresh) { tab = 1; refresh++ }
                1 -> GalleryScreen(activity, refresh) { refresh++ }
                2 -> SettingsScreen(activity) { refresh++ }
            }
        }
    }
}
