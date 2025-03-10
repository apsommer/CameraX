package com.sommerengineering.camerax

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

fun captureVideo(
    videoCapture: VideoCapture<Recorder>,
    context: Context) {

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Videos/CameraX")
    }

    val outputOptions = MediaStoreOutputOptions
        .Builder(
            context.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        .setContentValues(contentValues)
        .build()

    var recording: Recording? = null
    recording = videoCapture.output
        .prepareRecording(
            context,
            outputOptions)
        .apply {
            if (PermissionChecker.checkSelfPermission(
                    context,
                    android.Manifest.permission.RECORD_AUDIO)
                        == PermissionChecker.PERMISSION_GRANTED) {
                withAudioEnabled()
            }
        }
        .start(ContextCompat.getMainExecutor(context)) {
            when(it) {
                is VideoRecordEvent.Start -> {
                    // toggle button text
                }
                is VideoRecordEvent.Finalize -> {
                    if (it.hasError()) { Log.d(TAG, "captureVideo() error: ${it.error}") }
                    else {
                        Log.d(TAG, "captureVideo(): ${it.outputResults.outputUri}")
                        recording?.close()
                    }
                }
            }
            // toggle button
        }
}