package com.sommerengineering.camerax

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

typealias LumaListener = (luma: Double) -> Unit

class LuminosityAnalyzer(
    private val listener: LumaListener
) : ImageAnalysis.Analyzer {

    // convert byte array to byte buffer
    private fun ByteBuffer.toByteArray() : ByteArray {

        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }

    // determine average luminosity of image
    override fun analyze(image: ImageProxy) {

        val buffer = image.planes[0].buffer
        val data = buffer.toByteArray()
        val pixels = data.map { it.toInt() and 0xFF }
        val luma = pixels.average()

        listener(luma)
        image.close()
    }
}