package com.sommerengineering.camerax

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
typealias LumaListener = (luma: Double) -> Unit

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // request camera permissions
        if (allPermissionsGranted()) startCamera()
        else {
            ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS)
        }

        // listen to button click
        camera_capture_button.setOnClickListener { takePhoto() }

        // get directory for saved image
        outputDirectory = getOutputDirectory()

        // single thread executor
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    // capture image
    private fun takePhoto() {

        // get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture?: return // will be null if attempt to take photo before camera initialized!

        // create time-stamped output file to hold the image
        val photoFile = File(
                outputDirectory,
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                        .format(System.currentTimeMillis()) + ".jpg")

        // create output options which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // listen to image capture
        imageCapture.takePicture(
                outputOptions, // file options
                ContextCompat.getMainExecutor(this), // execute on main thread
                object : ImageCapture.OnImageSavedCallback {

                    // success
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                        // toast and log file location
                        val savedUri = Uri.fromFile(photoFile)
                        val msg = "Photo capture succeeded: $savedUri"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, msg)
                    }

                    // error on capture or save
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }
        })
    }

    // initialize the camera
    private fun startCamera() {

        // create camera future
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // listen to future
        cameraProviderFuture.addListener(Runnable {

            // get reference to provider, used to bind camera lifecycle to activity lifecycle
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // create preview and associate it to xml viewfinder
            val preview = Preview.Builder().build()
                    .also { it.setSurfaceProvider(viewFinder.createSurfaceProvider()) }

            // create image capture use case
            imageCapture = ImageCapture.Builder().build()

            // set back camera as default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {

                // clear any previous bindings
                cameraProvider.unbindAll()

                // bind lifecycles of all use cases to this activity lifecycle
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) { Log.e(TAG, "Use case binding failed", exc) }

        }, ContextCompat.getMainExecutor(this)) // main thread executor
    }

    private fun getOutputDirectory(): File {

        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(

            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray) {

        // validate request code
        if (requestCode == REQUEST_CODE_PERMISSIONS) {

            // permissions granted, start camera
            if (allPermissionsGranted()) startCamera()

            // not granted, toast message
            else {

                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}