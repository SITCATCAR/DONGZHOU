package com.swx.dongzhou.pages.scanPage

import android.annotation.SuppressLint
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.swx.dongzhou.BaseFragment
import com.swx.dongzhou.HistoryDatabase.History
import com.swx.dongzhou.HistoryDatabase.HistoryDatabase
import com.swx.dongzhou.Util.QRCodeType
import com.swx.dongzhou.databinding.ScanFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanFragment : BaseFragment<ScanFragmentBinding>(ScanFragmentBinding::inflate) {

    private var currentZoomRatio: Float = 1.0f
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var isFlashEnabled = false
    private var isCameraStarting = false
    private var isCameraStarted = false
    private var isScanning = false
    private var lastScanValue = ""
    private var lastScanTime = 0L
    private lateinit var cameraExecutor: ExecutorService
    private var scanner: BarcodeScanner? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { fetchImageFromUri(it) }
    }

    override fun loadData() {
    }

    override fun initView() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        scanner = BarcodeScanning.getClient()
        initActionButtons()
        initZoomSeekBar()
        requestCameraPermission()
    }

    private fun initActionButtons() {
        binding.btnGallery.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        binding.btnFlash.setOnClickListener {
            toggleFlash()
        }
        binding.btnBatch.setOnClickListener {
            Toast.makeText(requireContext(), "Batch scan is not ready", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initZoomSeekBar() {
        binding.zoomSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                applyZoom(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnZoomReduce.setOnClickListener { adjustZoom(-10) }
        binding.btnZoomAdd.setOnClickListener { adjustZoom(10) }
    }

    private fun adjustZoom(delta: Int) {
        val newProgress = (binding.zoomSeekBar.progress + delta)
            .coerceIn(0, binding.zoomSeekBar.max)
        binding.zoomSeekBar.progress = newProgress
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        if (isCameraStarted || isCameraStarting) {
            return
        }
        isCameraStarting = true
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            if (!isBindingAvailable) {
                isCameraStarting = false
                return@addListener
            }
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { preview ->
                preview.setSurfaceProvider(binding.preView.surfaceProvider)
            }
            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analyzer ->
                    analyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageFromCamera(imageProxy)
                    }
                }
            try {
                cameraProvider?.unbindAll()
                camera = cameraProvider?.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
                isCameraStarting = false
                isCameraStarted = true
                updateFlashButtonState()
                applyZoom(binding.zoomSeekBar.progress)
            } catch (e: Exception) {
                Log.e("ScanFragment", "Use case binding failed", e)
                camera = null
                isCameraStarting = false
                updateFlashButtonState()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun toggleFlash() {
        val currentCamera = camera
        if (currentCamera?.cameraInfo?.hasFlashUnit() != true) {
            Toast.makeText(requireContext(), "Flash is not available", Toast.LENGTH_SHORT).show()
            return
        }
        isFlashEnabled = !isFlashEnabled
        currentCamera.cameraControl.enableTorch(isFlashEnabled)
    }

    private fun updateFlashButtonState() {
        if (!isBindingAvailable) {
            return
        }
        val hasFlash = camera?.cameraInfo?.hasFlashUnit() == true
        binding.btnFlash.isEnabled = hasFlash
        binding.btnFlash.alpha = if (hasFlash) 1.0f else DISABLED_BUTTON_ALPHA
        if (!hasFlash) {
            isFlashEnabled = false
        }
    }

    private fun applyZoom(progress: Int) {
        val zoomState = camera?.cameraInfo?.zoomState?.value
        val minZoomRatio = zoomState?.minZoomRatio ?: MIN_ZOOM_RATIO
        val maxZoomRatio = zoomState?.maxZoomRatio ?: MAX_ZOOM_RATIO
        currentZoomRatio = minZoomRatio + progress / 100f * (maxZoomRatio - minZoomRatio)
        camera?.cameraControl?.setZoomRatio(
            currentZoomRatio.coerceIn(minZoomRatio, maxZoomRatio)
        )
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageFromCamera(imageProxy: ImageProxy) {
        if (isScanning) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        isScanning = true
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val barcodeScanner = scanner
        if (barcodeScanner == null) {
            imageProxy.close()
            isScanning = false
            return
        }
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                handleBarcodes(barcodes, showNoResultToast = false)
            }
            .addOnFailureListener { e ->
                Log.e("ScanFragment", "Scanning failed", e)
                isScanning = false
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun fetchImageFromUri(uri: Uri) {
        if (isScanning) {
            return
        }
        val barcodeScanner = scanner
        if (barcodeScanner == null) {
            Toast.makeText(requireContext(), "Save scan failed", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            isScanning = true
            val image = InputImage.fromFilePath(requireContext(), uri)
            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    handleBarcodes(barcodes, showNoResultToast = true)
                }
                .addOnFailureListener { e ->
                    Log.e("ScanFragment", "Scanning failed", e)
                    Toast.makeText(requireContext(), "Save scan failed", Toast.LENGTH_SHORT).show()
                    isScanning = false
                }
        } catch (e: Exception) {
            Log.e("ScanFragment", "Fetch image failed", e)
            Toast.makeText(requireContext(), "Save scan failed", Toast.LENGTH_SHORT).show()
            isScanning = false
        }
    }

    private fun handleBarcodes(barcodes: List<Barcode>, showNoResultToast: Boolean) {
        val barcode = barcodes.firstOrNull()
        if (barcode == null) {
            if (showNoResultToast) {
                Toast.makeText(requireContext(), "No barcode found", Toast.LENGTH_SHORT).show()
            }
            isScanning = false
            return
        }
        val value = barcode.rawValue.orEmpty()
        if (value.isBlank() || shouldIgnoreSameScan(value)) {
            if (showNoResultToast) {
                Toast.makeText(requireContext(), "No barcode found", Toast.LENGTH_SHORT).show()
            }
            isScanning = false
            return
        }

        val history = History(
            title = value.take(30).ifBlank { "Scan Result" },
            content = value,
            type = getQRCodeType(barcode)
        )
        saveScanHistory(history)
    }

    private fun shouldIgnoreSameScan(value: String): Boolean {
        val now = System.currentTimeMillis()
        return value == lastScanValue && now - lastScanTime < SCAN_DEBOUNCE_TIME
    }

    private fun saveScanHistory(history: History) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    HistoryDatabase.getDatabase(requireContext()).HistoryDao().insert(history)
                }
                lastScanValue = history.content
                lastScanTime = System.currentTimeMillis()
                Toast.makeText(requireContext(), "Scan saved", Toast.LENGTH_SHORT).show()
                delay(SCAN_DEBOUNCE_TIME)
                isScanning = false
            } catch (e: Exception) {
                Log.e("ScanFragment", "Save scan failed", e)
                Toast.makeText(requireContext(), "Save scan failed", Toast.LENGTH_SHORT).show()
                isScanning = false
            }
        }
    }

    private fun getQRCodeType(barcode: Barcode): QRCodeType {
        val value = barcode.rawValue.orEmpty()
        return when (barcode.valueType) {
            Barcode.TYPE_URL -> getUrlQRCodeType(value)
            Barcode.TYPE_WIFI -> QRCodeType.WIFI
            Barcode.TYPE_CONTACT_INFO -> QRCodeType.Contact
            Barcode.TYPE_PHONE -> QRCodeType.Tel
            Barcode.TYPE_EMAIL -> QRCodeType.Email
            Barcode.TYPE_SMS -> QRCodeType.SMS
            Barcode.TYPE_CALENDAR_EVENT -> QRCodeType.Calendar
            Barcode.TYPE_TEXT -> QRCodeType.Text
            else -> QRCodeType.Text
        }
    }

    private fun getUrlQRCodeType(value: String): QRCodeType {
        val lowerValue = value.lowercase()
        return when {
            "youtube.com" in lowerValue || "youtu.be" in lowerValue -> QRCodeType.Youtube
            "facebook.com" in lowerValue || "fb.com" in lowerValue -> QRCodeType.FaceBook
            "instagram.com" in lowerValue -> QRCodeType.Instagram
            "twitter.com" in lowerValue || "x.com" in lowerValue -> QRCodeType.Twitter
            "spotify" in lowerValue -> QRCodeType.Spotify
            "paypal" in lowerValue -> QRCodeType.Paypal
            "wa.me" in lowerValue || "whatsapp" in lowerValue -> QRCodeType.WhatsApp
            "viber" in lowerValue -> QRCodeType.Viber
            else -> QRCodeType.Website
        }
    }

    fun openCamera() {
        requestCameraPermission()
    }

    fun closeCamera() {
        camera?.cameraControl?.enableTorch(false)
        imageAnalyzer?.clearAnalyzer()
        cameraProvider?.unbindAll()
        camera = null
        imageAnalyzer = null
        isFlashEnabled = false
        isCameraStarting = false
        isCameraStarted = false
        isScanning = false
        updateFlashButtonState()
    }

    override fun onResume() {
        super.onResume()
        isScanning = false
        if (!isHidden && isBindingAvailable) {
            openCamera()
        }
    }

    override fun onPause() {
        closeCamera()
        super.onPause()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!isBindingAvailable) {
            return
        }
        if (hidden) {
            closeCamera()
        } else {
            openCamera()
        }
    }

    override fun onDestroyView() {
        closeCamera()
        scanner?.close()
        scanner = null
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
        super.onDestroyView()
    }

    companion object {
        private const val MIN_ZOOM_RATIO = 1.0f
        private const val MAX_ZOOM_RATIO = 4.0f
        private const val DISABLED_BUTTON_ALPHA = 0.4f
        private const val SCAN_DEBOUNCE_TIME = 2500L
    }
}
