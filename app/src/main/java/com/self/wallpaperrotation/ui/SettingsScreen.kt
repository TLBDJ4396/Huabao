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
fun SettingsScreen(a: MainActivity, onChanged: () -> Unit, onOpenTags: () -> Unit) {
    val s = remember { AppSettings(a) }
    var lk by remember { mutableStateOf(s.setLockScreen) }
    var hm by remember { mutableStateOf(s.setHomeScreen) }
    var cp by remember { mutableStateOf(s.importAsCopy) }
    var sh by remember { mutableStateOf(false) }
    var tk by remember { mutableIntStateOf(0) }
    var msg by remember { mutableStateOf<String?>(null) }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card { Column(Modifier.padding(16.dp)) {
                Text("\u5e94\u7528\u8303\u56f4", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("\u8bbe\u7f6e\u9501\u5c4f"); Switch(checked = lk, onCheckedChange = { lk = it; s.setLockScreen = it })
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("\u8bbe\u7f6e\u4e3b\u5c4f"); Switch(checked = hm, onCheckedChange = { hm = it; s.setHomeScreen = it })
                }
            } }
        }
        item {
            Card { Column(Modifier.padding(16.dp)) {
                Text("\u9ed8\u8ba4\u6dfb\u52a0\u65b9\u5f0f", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (cp) "\u5bfc\u5165\u526f\u672c" else "\u4ec5\u5f15\u7528")
                    Switch(checked = cp, onCheckedChange = { cp = it; s.importAsCopy = it })
                }
            } }
        }
        item {
            Card { Column(Modifier.padding(16.dp)) {
                Text("\u6807\u7b7e\u7ba1\u7406", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Button(onClick = onOpenTags, modifier = Modifier.fillMaxWidth()) { Text("\u7ba1\u7406\u6807\u7b7e") }
            } }
        }
        item {
            Card { Column(Modifier.padding(16.dp)) {
                Text("\u5907\u4efd\u4e0e\u6062\u590d", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        a.exportBackup { uri ->
                            try {
                                val j = WallpaperRepository.exportBackup(a)
                                a.contentResolver.openOutputStream(uri)?.use { it.write(j.toByteArray(Charsets.UTF_8)) }
                                msg = "\u5bfc\u51fa\u6210\u529f"
                            } catch (e: Exception) { msg = "\u5bfc\u51fa\u5931\u8d25" }
                        }
                    }, modifier = Modifier.weight(1f)) { Text("\u5bfc\u51fa\u5907\u4efd") }
                    OutlinedButton(onClick = {
                        a.importBackup { uri ->
                            try {
                                val j = a.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) } ?: ""
                                val ok = WallpaperRepository.importBackup(a, j)
                                msg = if (ok) "\u5bfc\u5165\u6210\u529f" else "\u5bfc\u5165\u5931\u8d25"
                                if (ok) { tk++; onChanged() }
                            } catch (e: Exception) { msg = "\u5bfc\u5165\u5931\u8d25" }
                        }
                    }, modifier = Modifier.weight(1f)) { Text("\u5bfc\u5165\u5907\u4efd") }
                }
            } }
        }
        item {
            Card { Column(Modifier.padding(16.dp)) {
                Text("\u58c1\u7eb8\u5e93", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                val cnt = remember(tk) { WallpaperRepository.load(a).size }
                Text("\u5f53\u524d\uff1a$cnt \u5f20")
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { sh = true }) { Text("\u6e05\u7a7a\u58c1\u7eb8\u5e93") }
            } }
        }
        item {
            Card { Column(Modifier.padding(16.dp)) {
                Text("\u91cd\u65b0\u6ce8\u518c\u5b9a\u65f6", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    RotationScheduler.cancel(a); RotationScheduler.schedule(a)
                    NotificationHelper.show(a)
                }) { Text("\u91cd\u65b0\u6ce8\u518c") }
            } }
        }
    }
    if (sh) {
        AlertDialog(onDismissRequest = { sh = false },
            title = { Text("\u6e05\u7a7a\u58c1\u7eb8\u5e93\uff1f") },
            text = { Text("\u4e0d\u53ef\u6062\u590d") },
            confirmButton = { TextButton(onClick = {
                WallpaperRepository.clear(a)
                try { java.io.File(a.filesDir, "wallpapers").deleteRecursively() } catch (_: Exception) {}
                sh = false; tk++; onChanged()
            }) { Text("\u6e05\u7a7a") } },
            dismissButton = { TextButton(onClick = { sh = false }) { Text("\u53d6\u6d88") } })
    }
    msg?.let { m ->
        AlertDialog(onDismissRequest = { msg = null },
            title = { Text("\u63d0\u793a") },
            text = { Text(m) },
            confirmButton = { TextButton(onClick = { msg = null }) { Text("\u786e\u5b9a") } })
    }
}