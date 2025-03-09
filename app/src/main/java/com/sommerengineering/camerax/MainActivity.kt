package com.sommerengineering.camerax

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.sommerengineering.camerax.ui.theme.CameraXTheme
import java.util.concurrent.ExecutorService

const val TAG = "~"

class MainActivity : ComponentActivity() {

    private val requiredPermissions = mutableListOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO
    ).toTypedArray()

    private val cameraPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        // ensure all required permissions is granted
        var isPermissionsGranted = true
        permissions.entries.forEach {
            if (it.key in requiredPermissions && !it.value) {
                isPermissionsGranted = false
            }
        }

        if (!isPermissionsGranted) { Log.d(TAG, "User denied permissions") }
        else { startCamera() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // request camera permissions, if necessary
        val isPermissionsGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(baseContext, it) ==
                PackageManager.PERMISSION_GRANTED
        }

        if (!isPermissionsGranted) {
            cameraPermissionRequest.launch(requiredPermissions)
            return
        }

        startCamera()
    }

    private fun startCamera() {
        setContent { App() }
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    App()
}