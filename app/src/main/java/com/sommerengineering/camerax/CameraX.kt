package com.sommerengineering.camerax

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraXPreview() {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // init preview use case
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {

        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview)
        preview.surfaceProvider = previewView.surfaceProvider
    }

    AndroidView(
        factory = {
            previewView
        },
        modifier = Modifier.fillMaxSize())
}

suspend fun Context.getCameraProvider()
    : ProcessCameraProvider =

    suspendCoroutine { continuation ->
        ProcessCameraProvider
            .getInstance(this)
            .also {
                it.addListener(
                    {
                        continuation.resume(it.get())
                    },
                    ContextCompat.getMainExecutor(this))
            }
    }
