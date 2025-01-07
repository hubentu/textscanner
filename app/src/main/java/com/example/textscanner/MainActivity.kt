package com.example.textscanner

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.example.textscanner.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "TextScanner"
private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class MainActivity : AppCompatActivity() {
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageAnalyzer: ImageAnalysis
    private var recognizedText = ""
    private lateinit var cameraExecutor: ExecutorService

    private val width: Int
        get() = binding.viewFinder.width
    
    private val height: Int
        get() = binding.viewFinder.height

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        binding.captureButton.setOnClickListener {
            analyzeText()
        }

        binding.copyButton.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Recognized Text", recognizedText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun analyzeText() {
        // Get preview view dimensions
        val previewView = binding.viewFinder
        val viewWidth = previewView.width
        val viewHeight = previewView.height
        
        if (viewWidth == 0 || viewHeight == 0) {
            Log.e(TAG, "View dimensions not available")
            return
        }

        imageAnalyzer.clearAnalyzer()

        imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                
                // Get the dimensions of the target region view
                val targetRegionLocation = IntArray(2)
                binding.targetRegion.getLocationInWindow(targetRegionLocation)
                
                val targetRegionLeft = targetRegionLocation[0]
                val targetRegionTop = targetRegionLocation[1]
                val targetRegionWidth = binding.targetRegion.width
                val targetRegionHeight = binding.targetRegion.height
                
                // Calculate the target region relative to the preview view
                val previewLocation = IntArray(2)
                binding.viewFinder.getLocationInWindow(previewLocation)
                
                val relativeLeft = targetRegionLeft - previewLocation[0]
                val relativeTop = targetRegionTop - previewLocation[1]
                
                // Calculate scale factors between preview and actual image
                val scaleX = image.width.toFloat() / binding.viewFinder.width
                val scaleY = image.height.toFloat() / binding.viewFinder.height
                
                // Calculate the target region in image coordinates
                val scaledFixedRect = Rect(
                    (relativeLeft * scaleX).toInt(),
                    (relativeTop * scaleY).toInt(),
                    ((relativeLeft + targetRegionWidth) * scaleX).toInt(),
                    ((relativeTop + targetRegionHeight) * scaleY).toInt()
                )
                
                Log.d(TAG, "Preview size: ${binding.viewFinder.width}x${binding.viewFinder.height}")
                Log.d(TAG, "Image size: ${image.width}x${image.height}")
                Log.d(TAG, "Target region in preview: ($relativeLeft, $relativeTop, ${relativeLeft + targetRegionWidth}, ${relativeTop + targetRegionHeight})")
                Log.d(TAG, "Target region in image: ${scaledFixedRect}")

                val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                textRecognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        var textInRegion = ""
                        for (block in visionText.textBlocks) {
                            val blockFrame = block.boundingBox
                            if (blockFrame != null && scaledFixedRect.contains(blockFrame)) {
                                textInRegion += block.text + "\n"
                            }
                        }
                        recognizedText = textInRegion.trim()
                        runOnUiThread {
                            if (recognizedText.isNotEmpty()) {
                                binding.capturedText.text = recognizedText
                                Toast.makeText(this@MainActivity, "Text captured!", Toast.LENGTH_SHORT).show()
                            } else {
                                binding.capturedText.text = ""
                                Toast.makeText(this@MainActivity, "No text captured!", Toast.LENGTH_SHORT).show()
                            }
                            // Re-enable the capture button
                            binding.captureButton.isEnabled = true
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Text recognition failed: ${e.message}")
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Text recognition failed!", Toast.LENGTH_SHORT).show()
                            // Re-enable the capture button
                            binding.captureButton.isEnabled = true
                        }
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                        imageAnalyzer.clearAnalyzer()
                    }
            } else {
                imageProxy.close()
                runOnUiThread {
                    // Re-enable the capture button
                    binding.captureButton.isEnabled = true
                }
            }
        }
        // Disable the capture button while processing
        binding.captureButton.isEnabled = false
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
