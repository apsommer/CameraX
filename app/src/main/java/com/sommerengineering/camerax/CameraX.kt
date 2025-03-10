package com.sommerengineering.camerax

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.sommerengineering.camerax.ui.theme.CameraXTheme
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun App() {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // init camera use cases
    val preview = androidx.camera.core.Preview.Builder().build()
    val recorder = Recorder.Builder()
        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
        .build()
    val recording: Recording? = null

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val imageAnalyzer = remember { ImageAnalysis.Builder().build() }
    val videoCapture = remember { VideoCapture.withOutput(recorder) }

    LaunchedEffect(Unit) {

        // bind use cases to activity lifecycle
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            // imageCapture,
            // imageAnalyzer,
            videoCapture)

        // configure use cases
        preview.surfaceProvider = previewView.surfaceProvider
        imageAnalyzer.setAnalyzer(
            Executors.newSingleThreadExecutor(),
            LuminosityAnalyzer { luma ->
                Log.d(TAG, "App() called with: luma = $luma")
            })
    }

    CameraXTheme {
        Surface (
            modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 96.dp,
                        bottom = 48.dp,
                        start = 48.dp,
                        end = 48.dp
                    ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {

                // image capture
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { captureImage(imageCapture, context) }) {
                    Text(
                        text = "Capture image")
                }

                Spacer(
                    modifier = Modifier.size(48.dp))

                val recordingMutableState = remember { mutableStateOf(recording) }

                // video
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        captureVideo(
                            videoCapture,
                            recordingMutableState,
                            context) }) {

                    // toggle button text
                    val text =
                        if (recordingMutableState.value != null) "STOP capture video"
                        else "Start capture video"

                    Text(text = text)
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp),
                    color = MaterialTheme.colorScheme.onSurface) {

                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

suspend fun Context.getCameraProvider()
    : ProcessCameraProvider =

    suspendCoroutine { continuation ->
        ProcessCameraProvider
            .getInstance(this)
            .also {
                it.addListener(
                    { continuation.resume(it.get()) },
                    ContextCompat.getMainExecutor(this))
            }
    }