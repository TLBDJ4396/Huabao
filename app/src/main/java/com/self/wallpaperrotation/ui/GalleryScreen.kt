package com.self.wallpaperrotation.ui
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.self.wallpaperrotation.MainActivity
import com.self.wallpaperrotation.data.AppSettings
import com.self.wallpaperrotation.data.WallpaperItem
import com.self.wallpaperrotation.data.WallpaperRepository
import com.self.wallpaperrotation.data.WallpaperType
import com.self.wallpaperrotation.wallpaper.ImageImporter
import java.io.File

@Composable
fun GalleryScreen(a: MainActivity, rk: Int, onOpenPreview: (WallpaperItem) -> Unit, onChanged: () -> Unit) {
    var list by remember(rk) { mutableStateOf(WallpaperRepository.load(a)) }
    Scaffold(floatingActionButton = {
        ExtendedFloatingActionButton(onClick = {
            a.pickImages { uris ->
                val s = AppSettings(a)
                uris.forEach { u: Uri ->
                    if (s.importAsCopy) ImageImporter.importCopy(a, u) else ImageImporter.addReference(a, u)
                }
                list = WallpaperRepository.load(a)
                onChanged()
            }
        }, icon = { Icon(Icons.Default.Add, contentDescription = null) }, text = { Text("\u6dfb\u52a0") })
    }) { p ->
        if (list.isEmpty()) {
            Box(Modifier.padding(p).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("\u8fd8\u6ca1\u6709\u58c1\u7eb8\uff0c\u70b9\u53f3\u4e0b\u89d2\u6dfb\u52a0", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(3),
                modifier = Modifier.padding(p).fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(list, key = { it.id }) { it ->
                    val m: Any = if (it.type == WallpaperType.COPY) File(it.source) else Uri.parse(it.source)
                    Box(Modifier.aspectRatio(0.6f).clip(RoundedCornerShape(8.dp)).clickable { onOpenPreview(it) }) {
                        AsyncImage(model = m, contentDescription = null,
                            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}