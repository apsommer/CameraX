package com.sommerengineering.camerax

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.camera.core.ImageCapture
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.sommerengineering.camerax.ui.theme.CameraXTheme
import java.util.concurrent.ExecutorService

class MainActivity : ComponentActivity() {

    // init camera
    lateinit var imageCapture: ImageCapture
    lateinit var videoCapture: VideoCapture<Recorder>
    lateinit var recording: Recording
    lateinit var executor: ExecutorService

    // define permissions
    private val requiredPermissions = mutableListOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO
    ).toTypedArray()

    private val cameraPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            var isPermissionsGranted = true
            permissions.entries.forEach {
                if (it.key in requiredPermissions && !it.value) {
                    isPermissionsGranted = false
                }
            }

            if (!isPermissionsGranted) {
                // permissions denied by user
            } else {
                startCamera()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ensure all camera permission are granted
        val isPermissionsGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(baseContext, it) ==
                PackageManager.PERMISSION_GRANTED
        }
        if (isPermissionsGranted) { startCamera() }
        else { cameraPermissionRequest.launch(requiredPermissions) }
        
        setContent {
            CameraXTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
    
    fun startCamera() {
        // todo
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CameraXTheme {
        Greeting("Android")
    }
}