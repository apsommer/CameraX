package com.sommerengineering.camerax

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
