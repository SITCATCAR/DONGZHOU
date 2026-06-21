package com.swx.dongzhou.pages.scanPage

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.swx.dongzhou.BaseFragment
import com.swx.dongzhou.databinding.ScanFragmentBinding

class ScanFragment : BaseFragment<ScanFragmentBinding>(ScanFragmentBinding::inflate) {

    private var currentZoomRatio: Float = 1.0f
    private var cameraProvider: ProcessCameraProvider? = null
    private var isCameraStarted = false

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun loadData() {
    }

    override fun initView() {
        initActionButtons()
        initZoomSeekBar()
        requestCameraPermission()
    }

    private fun initActionButtons() {
        binding.btnGallery.setOnClickListener {
        }
        binding.btnFlash.setOnClickListener {
        }
    }

    private fun initZoomSeekBar() {
        binding.zoomSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentZoomRatio = 1.0f + progress / 100f * (MAX_ZOOM_RATIO - 1.0f)
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
        if (isCameraStarted) {
            return
        }
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            if (!isBindingAvailable) {
                return@addListener
            }
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { preview ->
                preview.setSurfaceProvider(binding.preView.surfaceProvider)
            }
            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview
                )
                isCameraStarted = true
            } catch (e: Exception) {
                Log.e("ScanFragment", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    fun openCamera() {
        requestCameraPermission()
    }

    fun closeCamera() {
        cameraProvider?.unbindAll()
        isCameraStarted = false
    }

    override fun onDestroyView() {
        closeCamera()
        super.onDestroyView()
    }

    companion object {
        private const val MAX_ZOOM_RATIO = 4.0f
    }
}
