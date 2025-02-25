package com.sommerengineering.camerax

import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner

@Composable
fun CameraXPreview(
    modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // init preview use case
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val cameraProvider = remember { ProcessCameraProvider.getInstance(context).get() }

    LaunchedEffect(Unit) {

    }
}