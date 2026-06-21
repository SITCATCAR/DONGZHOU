package com.swx.dongzhou.pages.scanPage

import android.widget.SeekBar
import com.swx.dongzhou.BaseFragment
import com.swx.dongzhou.databinding.ScanFragmentBinding


class ScanFragment : BaseFragment<ScanFragmentBinding>(ScanFragmentBinding::inflate) {

    //当前缩放比例，1.0f 表示 1x
    private var currentZoomRatio: Float = 1.0f

    override fun loadData() {
    }

    override fun initView() {
        initActionButtons()
        initZoomSeekBar()
        // TODO 启动相机时初始化 CameraX 并把 cameraPlaceholder 替换为 PreviewView
    }

    private fun initActionButtons() {
        binding.btnGallery.setOnClickListener {
            // TODO 跳转系统相册选择图片并识别
        }
        // 手电筒
        binding.btnFlash.setOnClickListener {
            // TODO 切换手电筒开关状态
        }

    }

    private fun initZoomSeekBar() {
        binding.zoomSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentZoomRatio = 1.0f + progress / 100f * (MAX_ZOOM_RATIO - 1.0f)
                // TODO 启动相机后通过 缩放实现
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

    companion object {
        private const val MAX_ZOOM_RATIO = 4.0f
    }
}
