package com.swx.dongzhou.Util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QRCodeGenerator {

    private const val QR_SIZE = 1200
    private const val CHARACTER_SET = "UTF-8"
    const val MAX_CONTENT_BYTES = 2953

    fun getContentByteCount(content: String): Int {
        return content.toByteArray(Charsets.UTF_8).size
    }

    fun canEncodeContent(content: String): Boolean {
        return getContentByteCount(content) <= MAX_CONTENT_BYTES
    }

    fun generateQRCode(content: String): Bitmap? {
        if (!canEncodeContent(content)) {
            return null
        }

        return try {
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to CHARACTER_SET,
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L
            )
            val bitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                QR_SIZE,
                QR_SIZE,
                hints
            )

            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                for (x in 0 until width) {
                    pixels[y * width + x] = if (bitMatrix[x, y]) {
                        Color.BLACK
                    } else {
                        Color.WHITE
                    }
                }
            }

            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

}
