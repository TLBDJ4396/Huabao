package com.self.wallpaperrotation.ui

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun GalleryScreen(activity: MainActivity, refreshKey: Int, onChanged: () -> Unit) {
    var list by remember(refreshKey) { mutableStateOf(WallpaperRepository.load(activity)) }
    var pendingDelete by remember { mutableStateOf<WallpaperItem?>(null) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    activity.pickImages { uris ->
                        val settings = AppSettings(activity)
                        var added = 0
                        uris.forEach { uri: Uri ->
                            val ok = if (settings.importAsCopy) {
                                ImageImporter.importCopy(activity, uri)
                            } else {
                                ImageImporter.addReference(activity, uri)
                            }
                            if (ok) added++
                        }
                        list = WallpaperRepository.load(activity)
                        onChanged()
                    }
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("娣诲姞") }
            )
        }
    ) { padding ->
        if (list.isEmpty()) {
            Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("杩樻病鏈夊绾革紝鐐瑰彸涓嬭娣诲姞", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(list, key = { it.id }) { item ->
                    val model: Any = if (item.type == WallpaperType.COPY) {
                        File(item.source)
                    } else {
                        Uri.parse(item.source)
                    }
                    Box(
                        Modifier
                            .aspectRatio(0.6f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { pendingDelete = item }
                    ) {
                        AsyncImage(
                            model = model,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    pendingDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("鍒犻櫎杩欏紶澹佺焊锛?) },
            text = { Text(if (item.type == WallpaperType.COPY) "鍓湰鏂囦欢涔熶細涓€骞跺垹闄? else "鍙Щ闄よ褰曪紝鍘熷浘涓嶅彈褰卞搷") },
            confirmButton = {
                TextButton(onClick = {
                    WallpaperRepository.remove(activity, item.id)
                    list = WallpaperRepository.load(activity)
                    pendingDelete = null
                    onChanged()
                }) { Text("鍒犻櫎") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("鍙栨秷") }
            }
        )
    }
}
