package com.self.wallpaperrotation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.self.wallpaperrotation.data.AppSettings
import com.self.wallpaperrotation.notification.NotificationHelper
import com.self.wallpaperrotation.rotation.RotationScheduler
import com.self.wallpaperrotation.ui.MainScreen

class MainActivity : ComponentActivity() {

    private var pendingCallback: ((List<Uri>) -> Unit)? = null

    private val pickLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        pendingCallback?.invoke(uris)
        pendingCallback = null
    }

    fun pickImages(callback: (List<Uri>) -> Unit) {
        pendingCallback = callback
        pickLauncher.launch(arrayOf("image/*"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainScreen(this)
                }
            }
        }
        NotificationHelper.show(this)
        val settings = AppSettings(this)
        if (settings.rotationEnabled) {
            RotationScheduler.schedule(this)
        }
        requestIgnoreBatteryOptimizations()
    }

    private fun requestIgnoreBatteryOptimizations() {
        try {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                startActivity(
                    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                    }
                )
            }
        } catch (_: Exception) {
        }
    }
}
