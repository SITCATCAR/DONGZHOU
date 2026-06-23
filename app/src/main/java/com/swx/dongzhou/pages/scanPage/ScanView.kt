package com.swx.dongzhou.pages.scanPage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt

class ScanView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var scanLineColor: Int = try {
        "#800188FE".toColorInt()
    } catch (e: Exception) {
        Color.argb(128, 16, 185, 129)
    }
    private val laserPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private var laserY = 0f

    private val laserSpeed = dpToPx(1.5f)

    private val laserHeightRatio = 0.25f

    private var shader: LinearGradient? = null

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val laserHeight = height * laserHeightRatio

        // 超出后重置
        if (laserY > height - laserHeight) {
            laserY = 0f
        }

        // 更新渐变
        shader = LinearGradient(0f, laserY + laserHeight, 0f, laserY,
            intArrayOf(
                scanLineColor,
                Color.TRANSPARENT
            ),
            null,
            Shader.TileMode.CLAMP
        )

        laserPaint.shader = shader

        // 绘制扫描区域
        canvas.drawRect(
            0f,
            laserY,
            width,
            laserY + laserHeight,
            laserPaint
        )

        laserY += laserSpeed
        postInvalidateOnAnimation()
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}