package com.sommerengineering.camerax

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat

fun captureImage(
    imageCapture: ImageCapture,
    context: Context
) {

    // configure file options
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX")
    }
    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    // capture image
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object  : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.d(TAG, "onImageSaved() called with: outputFileResults = $outputFileResults")
            }

            override fun onError(exception: ImageCaptureException) {
                Log.d(TAG, "onError() called with: exception = $exception")
            }
        })
}