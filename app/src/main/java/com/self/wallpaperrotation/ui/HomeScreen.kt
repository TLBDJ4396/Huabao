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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.self.wallpaperrotation.MainActivity
import com.self.wallpaperrotation.data.AppSettings
import com.self.wallpaperrotation.data.TagRepository
import com.self.wallpaperrotation.data.WallpaperRepository
import com.self.wallpaperrotation.notification.NotificationHelper
import com.self.wallpaperrotation.rotation.RotationReceiver
import com.self.wallpaperrotation.rotation.RotationScheduler

@Composable
fun HomeScreen(a: MainActivity, rk: Int, onGoGallery: () -> Unit) {
    val s = remember { AppSettings(a) }
    var on by remember(rk) { mutableIntStateOf(if (s.rotationEnabled) 1 else 0) }
    var iv by remember(rk) { mutableIntStateOf(s.intervalMinutes) }
    val cnt = remember(rk) { WallpaperRepository.load(a).size }
    val tags = remember(rk) { TagRepository.load(a) }
    val ah = tags.find { it.id == s.activeHomeTagId }
    val al = tags.find { it.id == s.activeLockTagId }
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Card { Column(Modifier.padding(16.dp)) {
            Text("\u8f6e\u6362\u5f00\u5173", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Switch(checked = on == 1, onCheckedChange = { v ->
                on = if (v) 1 else 0; s.rotationEnabled = v
                if (v) RotationScheduler.schedule(a) else RotationScheduler.cancel(a)
                NotificationHelper.show(a)
            })
        } } }
        item { Card { Column(Modifier.padding(16.dp)) {
            Text("\u68c0\u67e5\u95f4\u9694", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            val opts = listOf(15, 30, 60, 120, 180)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                opts.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach { m ->
                            if (iv == m) Button(onClick = { iv = m; s.intervalMinutes = m; RotationScheduler.schedule(a); NotificationHelper.show(a) }, modifier = Modifier.weight(1f)) { Text("$m") }
                            else OutlinedButton(onClick = { iv = m; s.intervalMinutes = m; RotationScheduler.schedule(a); NotificationHelper.show(a) }, modifier = Modifier.weight(1f)) { Text("$m") }
                        }
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        } } }
        item { Card { Column(Modifier.padding(16.dp)) {
            Text("\u5f53\u524d\u6807\u7b7e", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("\u4e3b\u5c4f\uff1a${ah?.name ?: "\u5168\u90e8"}")
            Text("\u9501\u5c4f\uff1a${al?.name ?: "\u5168\u90e8"}")
        } } }
        item { Card { Column(Modifier.padding(16.dp)) {
            Text("\u58c1\u7eb8\u5e93\uff1a$cnt \u5f20", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onGoGallery, modifier = Modifier.fillMaxWidth()) { Text("\u53bb\u56fe\u5e93\u6dfb\u52a0") }
        } } }
        item { Card { Column(Modifier.padding(16.dp)) {
            Text("\u624b\u52a8\u64cd\u4f5c", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                a.sendBroadcast(android.content.Intent(a, RotationReceiver::class.java).apply { action = RotationReceiver.ACTION_NEXT })
            }, modifier = Modifier.fillMaxWidth()) { Text("\u7acb\u5373\u6362\u4e0b\u4e00\u5f20") }
        } } }
    }
}