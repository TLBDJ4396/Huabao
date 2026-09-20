package com.self.wallpaperrotation.ui
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.self.wallpaperrotation.MainActivity
import com.self.wallpaperrotation.data.AppSettings
import com.self.wallpaperrotation.data.TagRepository
import com.self.wallpaperrotation.data.TagScope
import com.self.wallpaperrotation.data.WallpaperItem
import com.self.wallpaperrotation.data.WallpaperRepository
import com.self.wallpaperrotation.data.WallpaperType
import com.self.wallpaperrotation.notification.NotificationHelper
import com.self.wallpaperrotation.rotation.RotationScheduler
import com.self.wallpaperrotation.wallpaper.WallpaperSetter
import java.io.File

@Composable
fun PreviewScreen(a: MainActivity, item: WallpaperItem, onBack: () -> Unit, onChanged: () -> Unit) {
    var ci by remember { mutableStateOf(item) }
    var off by remember { mutableFloatStateOf(item.cropOffset) }
    var sDel by remember { mutableStateOf(false) }
    var sTag by remember { mutableStateOf(false) }
    val m: Any = if (ci.type == WallpaperType.COPY) File(ci.source) else Uri.parse(ci.source)
    val lbl = when { off < 0.34f -> "\u9876\u90e8"; off > 0.66f -> "\u5e95\u90e8"; else -> "\u5c45\u4e2d" }
    Column(Modifier.fillMaxSize().background(Color.Black)) {
        Box(Modifier.fillMaxWidth().weight(1f).pointerInput(ci.id) {
            detectDragGestures { ch, d ->
                ch.consume()
                off = (off + d.y / 600f).coerceIn(0f, 1f)
            }
        }) {
            AsyncImage(model = m, contentDescription = null, contentScale = ContentScale.Crop,
                alignment = BiasAlignment(0f, off * 2f - 1f), modifier = Modifier.fillMaxSize())
            Text("\u6ed1\u52a8\u8c03\u6574\u4f4d\u7f6e\uff1a$lbl", color = Color.White,
                modifier = Modifier.align(Alignment.TopCenter).padding(16.dp))
        }
        Column(Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val u = ci.copy(cropOffset = off)
                    WallpaperRepository.update(a, u); ci = u
                    WallpaperSetter.apply(a, u.source, u.type == WallpaperType.COPY, true, true, off)
                    val s = AppSettings(a); s.rotationEnabled = false
                    RotationScheduler.cancel(a); NotificationHelper.show(a); onChanged()
                }, modifier = Modifier.weight(1f)) { Text("\u8bbe\u4e3a\u5f53\u524d\u58c1\u7eb8") }
                OutlinedButton(onClick = {
                    val u = ci.copy(cropOffset = off)
                    WallpaperRepository.update(a, u); ci = u
                    WallpaperRepository.setCursorIndex(a, u.id)
                    val s = AppSettings(a); s.rotationEnabled = true
                    RotationScheduler.schedule(a)
                    WallpaperSetter.apply(a, u.source, u.type == WallpaperType.COPY, s.setLockScreen, s.setHomeScreen, off)
                    NotificationHelper.show(a); onChanged()
                }, modifier = Modifier.weight(1f)) { Text("\u4ece\u8fd9\u91cc\u8f6e\u6362") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { sTag = true }, modifier = Modifier.weight(1f)) { Text("\u6253\u6807\u7b7e") }
                OutlinedButton(onClick = { sDel = true }, modifier = Modifier.weight(1f)) { Text("\u5220\u9664", color = Color.Red) }
            }
            Spacer(Modifier.height(4.dp))
            Button(onClick = {
                WallpaperRepository.update(a, ci.copy(cropOffset = off)); onBack()
            }, modifier = Modifier.fillMaxWidth()) { Text("\u8fd4\u56de") }
        }
    }
    if (sDel) {
        AlertDialog(onDismissRequest = { sDel = false },
            title = { Text("\u5220\u9664\uff1f") },
            text = { Text(if (ci.type == WallpaperType.COPY) "\u526f\u672c\u4e5f\u5220" else "\u53ea\u79fb\u9664\u8bb0\u5f55") },
            confirmButton = { TextButton(onClick = { WallpaperRepository.remove(a, ci.id); sDel = false; onChanged(); onBack() }) { Text("\u5220\u9664") } },
            dismissButton = { TextButton(onClick = { sDel = false }) { Text("\u53d6\u6d88") } })
    }
    if (sTag) {
        val allTags = remember { TagRepository.load(a) }
        var sel by remember { mutableStateOf(ci.tags.toSet()) }
        AlertDialog(onDismissRequest = { sTag = false },
            title = { Text("\u9009\u6807\u7b7e") },
            text = { Column {
                if (allTags.isEmpty()) Text("\u65e0\u6807\u7b7e")
                else allTags.forEach { t ->
                    val ck = sel.contains(t.id)
                    TextButton(onClick = { sel = if (ck) sel - t.id else sel + t.id }) {
                        Text((if (ck) "[x] " else "[ ] ") + t.name)
                    }
                }
            } },
            confirmButton = { TextButton(onClick = {
                val u = ci.copy(tags = sel.toList(), cropOffset = off)
                WallpaperRepository.update(a, u); ci = u; sTag = false; onChanged()
            }) { Text("\u786e\u5b9a") } },
            dismissButton = { TextButton(onClick = { sTag = false }) { Text("\u53d6\u6d88") } })
    }
}