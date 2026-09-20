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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.self.wallpaperrotation.MainActivity
import com.self.wallpaperrotation.data.AppSettings
import com.self.wallpaperrotation.data.Tag
import com.self.wallpaperrotation.data.TagRepository
import com.self.wallpaperrotation.data.TagScope

@Composable
fun TagsScreen(a: MainActivity, onBack: () -> Unit) {
    var tags by remember { mutableStateOf(TagRepository.load(a)) }
    var tk by remember { mutableIntStateOf(0) }
    var showAdd by remember { mutableStateOf(false) }
    var nn by remember { mutableStateOf("") }
    var ns by remember { mutableStateOf(TagScope.BOTH) }
    var cfMsg by remember { mutableStateOf<String?>(null) }
    var pend by remember { mutableStateOf<Tag?>(null) }

    fun applyTag(t: Tag) {
        TagRepository.add(a, t); tags = TagRepository.load(a); tk++
    }
    fun checkAndAdd(t: Tag) {
        val s = AppSettings(a)
        val ex = tags.filter { it.enabled }
        val hc = (t.scope == TagScope.HOME || t.scope == TagScope.BOTH) && ex.any { it.id == s.activeHomeTagId }
        val lc = (t.scope == TagScope.LOCK || t.scope == TagScope.BOTH) && ex.any { it.id == s.activeLockTagId }
        if (hc || lc) {
            pend = t
            val txt = when { hc && lc -> "\u4e3b\u5c4f\u548c\u9501\u5c4f"; hc -> "\u4e3b\u5c4f"; else -> "\u9501\u5c4f" }
            cfMsg = "\u8be5\u8303\u56f4\uff08$txt\uff09\u5df2\u88ab\u5360\u7528\uff0c\u662f\u5426\u66ff\u6362\uff1f"
        } else applyTag(t)
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("\u6807\u7b7e\u7ba1\u7406", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        Button(onClick = { showAdd = true; nn = ""; ns = TagScope.BOTH },
            modifier = Modifier.fillMaxWidth()) { Text("\u65b0\u5efa\u6807\u7b7e") }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 4.dp)) {
            items(tags, key = { it.id }) { t ->
                Card { Column(Modifier.padding(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(t.name, style = MaterialTheme.typography.titleMedium)
                            Text("\u8303\u56f4\uff1a" + when (t.scope) {
                                TagScope.HOME -> "\u4e3b\u5c4f"; TagScope.LOCK -> "\u9501\u5c4f"; TagScope.BOTH -> "\u540c\u65f6" },
                                style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = {
                            TagRepository.remove(a, t.id)
                            tags = TagRepository.load(a); tk++
                        }) { Text("\u5220\u9664") }
                    }
                } }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("\u8fd4\u56de") }
    }

    if (showAdd) {
        AlertDialog(onDismissRequest = { showAdd = false },
            title = { Text("\u65b0\u5efa\u6807\u7b7e") },
            text = { Column {
                OutlinedTextField(value = nn, onValueChange = { nn = it },
                    label = { Text("\u540d\u79f0") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(TagScope.HOME to "\u4e3b\u5c4f", TagScope.LOCK to "\u9501\u5c4f", TagScope.BOTH to "\u540c\u65f6").forEach { (sc, l) ->
                        if (ns == sc) Button(onClick = { ns = sc }) { Text(l) }
                        else OutlinedButton(onClick = { ns = sc }) { Text(l) }
                    }
                }
            } },
            confirmButton = { TextButton(onClick = {
                if (nn.isNotBlank()) { checkAndAdd(Tag(name = nn.trim(), scope = ns)); showAdd = false }
            }) { Text("\u521b\u5efa") } },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("\u53d6\u6d88") } })
    }
    cfMsg?.let { m ->
        AlertDialog(onDismissRequest = { cfMsg = null; pend = null },
            title = { Text("\u8303\u56f4\u51b2\u7a81") },
            text = { Text(m) },
            confirmButton = { TextButton(onClick = {
                pend?.let { applyTag(it) }; cfMsg = null; pend = null
            }) { Text("\u66ff\u6362") } },
            dismissButton = { TextButton(onClick = { cfMsg = null; pend = null }) { Text("\u53d6\u6d88") } })
    }
}