package com.self.wallpaperrotation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.self.wallpaperrotation.rotation.RotationReceiver

@Composable
fun HomeScreen(activity: MainActivity, refreshKey: Int, onGoGallery: () -> Unit) {
    val settings = remember { AppSettings(activity) }
    var enabled by remember(refreshKey) { mutableIntStateOf(if (settings.rotationEnabled) 1 else 0) }
    var interval by remember(refreshKey) { mutableIntStateOf(settings.intervalMinutes) }

    val count = remember(refreshKey) { WallpaperRepository.load(activity).size }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("杞崲寮€鍏?, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Switch(
                        checked = enabled == 1,
                        onCheckedChange = { v ->
                            enabled = if (v) 1 else 0
                            settings.rotationEnabled = v
                            if (v) {
                                RotationScheduler.schedule(activity)
                            } else {
                                RotationScheduler.cancel(activity)
                            }
                            NotificationHelper.show(activity)
                        }
                    )
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("妫€鏌ラ棿闅?, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    val opts = listOf(15, 30, 60, 120, 180)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        opts.chunked(3).forEach { row ->
                            androidx.compose.foundation.layout.Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                row.forEach { m ->
                                    if (interval == m) {
                                        Button(onClick = {
                                            interval = m
                                            settings.intervalMinutes = m
                                            RotationScheduler.schedule(activity)
                                            NotificationHelper.show(activity)
                                        }, modifier = Modifier.weight(1f)) {
                                            Text("${m}鍒?)
                                        }
                                    } else {
                                        OutlinedButton(onClick = {
                                            interval = m
                                            settings.intervalMinutes = m
                                            RotationScheduler.schedule(activity)
                                            NotificationHelper.show(activity)
                                        }, modifier = Modifier.weight(1f)) {
                                            Text("${m}鍒?)
                                        }
                                    }
                                }
                                repeat(3 - row.size) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("澹佺焊搴擄細$count 寮?, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onGoGallery, modifier = Modifier.fillMaxWidth()) {
                        Text("鍘诲浘搴撴坊鍔?)
                    }
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("鎵嬪姩鎿嶄綔", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val intent = android.content.Intent(activity, RotationReceiver::class.java).apply {
                                action = RotationReceiver.ACTION_NEXT
                            }
                            activity.sendBroadcast(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("绔嬪嵆鎹笅涓€寮?)
                    }
                }
            }
        }
    }
}
