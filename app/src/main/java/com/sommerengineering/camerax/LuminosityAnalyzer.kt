package com.sommerengineering.camerax

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

    

    override fun analyze(image: ImageProxy) {
        TODO("Not yet implemented")
    }
}