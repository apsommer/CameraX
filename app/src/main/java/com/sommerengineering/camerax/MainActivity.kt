package com.sommerengineering.camerax

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.sommerengineering.camerax.ui.theme.CameraXTheme

class MainActivity : ComponentActivity() {

    private val cameraPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            var isPermissionsGranted = true
            permissions.entries.forEach {
                if (it.key in requiredPermissions && it.value == false) {
                    isPermissionsGranted = false
                }
            }

            if (!isPermissionsGranted) {
                // permissions denied by user
            } else {
                // start camera
            }
        }

    private val requiredPermissions = mutableListOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO
    ).toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ensure camera permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionRequest.launch(requiredPermissions)
        }

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