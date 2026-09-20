package com.self.wallpaperrotation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.self.wallpaperrotation.MainActivity
import com.self.wallpaperrotation.data.AppSettings
import com.self.wallpaperrotation.data.WallpaperRepository
import com.self.wallpaperrotation.notification.NotificationHelper
import com.self.wallpaperrotation.rotation.RotationScheduler

@Composable
fun SettingsScreen(activity: MainActivity, onChanged: () -> Unit) {
    val settings = remember { AppSettings(activity) }
    var lock by remember { mutableStateOf(settings.setLockScreen) }
    var home by remember { mutableStateOf(settings.setHomeScreen) }
    var asCopy by remember { mutableStateOf(settings.importAsCopy) }
    var showClear by remember { mutableStateOf(false) }
    var tick by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("搴旂敤鑼冨洿", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("璁剧疆閿佸睆")
                        Switch(checked = lock, onCheckedChange = {
                            lock = it
                            settings.setLockScreen = it
                        })
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("璁剧疆涓诲睆")
                        Switch(checked = home, onCheckedChange = {
                            home = it
                            settings.setHomeScreen = it
                        })
                    }
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("榛樿娣诲姞鏂瑰紡", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (asCopy) "瀵煎叆鍓湰锛堢ǔ锛屽崰绌洪棿锛? else "浠呭紩鐢紙鐪佺┖闂达紝鏄撳け鏁堬級")
                        Switch(checked = asCopy, onCheckedChange = {
                            asCopy = it
                            settings.importAsCopy = it
                        })
                    }
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("澹佺焊搴?, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    val count = remember(tick) { WallpaperRepository.load(activity).size }
                    Text("褰撳墠锛?count 寮?)
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { showClear = true }) {
                        Text("娓呯┖澹佺焊搴?)
                    }
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("閲嶆柊娉ㄥ唽瀹氭椂", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("濡傛灉鎰熻瀹氭椂涓嶇敓鏁堬紝鐐逛笅闈㈡寜閽噸缃竴娆°€?, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        RotationScheduler.cancel(activity)
                        RotationScheduler.schedule(activity)
                        NotificationHelper.show(activity)
                    }) { Text("閲嶆柊娉ㄥ唽") }
                }
            }
        }
    }

    if (showClear) {
        AlertDialog(
            onDismissRequest = { showClear = false },
            title = { Text("娓呯┖澹佺焊搴擄紵") },
            text = { Text("鎵€鏈夎褰曞拰鍓湰鏂囦欢閮戒細琚垹闄わ紝涓嶅彲鎭㈠銆?) },
            confirmButton = {
                TextButton(onClick = {
                    WallpaperRepository.clear(activity)
                    try {
                        java.io.File(activity.filesDir, "wallpapers").deleteRecursively()
                    } catch (_: Exception) {}
                    showClear = false
                    tick++
                    onChanged()
                }) { Text("娓呯┖") }
            },
            dismissButton = {
                TextButton(onClick = { showClear = false }) { Text("鍙栨秷") }
            }
        )
    }
}
