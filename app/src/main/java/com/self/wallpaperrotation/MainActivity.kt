package com.self.wallpaperrotation
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.PickVisualMediaRequest
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.self.wallpaperrotation.data.AppSettings
import com.self.wallpaperrotation.notification.NotificationHelper
import com.self.wallpaperrotation.rotation.RotationReceiver
import com.self.wallpaperrotation.rotation.RotationScheduler
import com.self.wallpaperrotation.ui.MainScreen
class MainActivity : ComponentActivity() {
    private var pc: ((List<Uri>) -> Unit)? = null
    private var ec: ((Uri) -> Unit)? = null
    private var ic: ((Uri) -> Unit)? = null
    private val pick = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(100)) { u ->
        pc?.invoke(u); pc = null
    }
    private val create = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { u ->
        u?.let { ec?.invoke(it) }; ec = null
    }
    private val open = registerForActivityResult(ActivityResultContracts.OpenDocument()) { u ->
        u?.let { ic?.invoke(it) }; ic = null
    }
    fun pickImages(cb: (List<Uri>) -> Unit) {
        pc = cb
        pick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    fun exportBackup(cb: (Uri) -> Unit) { ec = cb; create.launch("huabao_backup.json") }
    fun importBackup(cb: (Uri) -> Unit) { ic = cb; open.launch(arrayOf("application/json", "*/*")) }
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        if (intent?.action == "com.self.wallpaperrotation.OPEN_NEXT") {
            sendBroadcast(Intent(this, RotationReceiver::class.java).apply { action = RotationReceiver.ACTION_NEXT })
            finish(); return
        }
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                    MainScreen(this)
                }
            }
        }
        NotificationHelper.show(this)
        if (AppSettings(this).rotationEnabled) RotationScheduler.schedule(this)
        try {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                })
            }
        } catch (_: Exception) {}
    }
}
class ShortcutActivity : Activity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        try {
            sendBroadcast(Intent(this, RotationReceiver::class.java).apply { action = RotationReceiver.ACTION_NEXT })
        } catch (_: Exception) {}
        finish()
    }
}