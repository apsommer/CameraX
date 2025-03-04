package com.sommerengineering.camerax

import android.content.Context
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
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

        val cameraProvider = getCameraProvider(context)
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview)
        preview.setSurfaceProvider { previewView.surfaceProvider }
    }
}

suspend fun getCameraProvider(
    context: Context)
: ProcessCameraProvider =

    suspendCoroutine { continuation ->
        ProcessCameraProvider
            .getInstance(context)
            .also {
                it.addListener(
                    {
                        continuation.resume(it.get())
                    },
                    ContextCompat.getMainExecutor(context))
            }
    }
