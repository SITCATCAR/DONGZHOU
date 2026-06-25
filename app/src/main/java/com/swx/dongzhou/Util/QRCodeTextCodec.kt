package com.swx.dongzhou.Util

import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object QRCodeTextCodec {

    private const val COMPRESSED_TEXT_PREFIX = "GZIP_Base64:"

    fun createQRCodeContent(content: String, type: QRCodeType): String? {
        if (type != QRCodeType.Text || QRCodeGenerator.canEncodeContent(content)) {
            return content.takeIf { QRCodeGenerator.canEncodeContent(it) }
        }

        val compressedContent = compressText(content)
        return compressedContent.takeIf { QRCodeGenerator.canEncodeContent(it) }
    }

    fun decodeQRCodeContent(content: String): String {
        if (!content.startsWith(COMPRESSED_TEXT_PREFIX)) {
            return content
        }
        return runCatching {
            val encodedContent = content.removePrefix(COMPRESSED_TEXT_PREFIX)
            val compressedBytes = Base64.decode(encodedContent, Base64.NO_WRAP)
            GZIPInputStream(ByteArrayInputStream(compressedBytes)).use { gzipInputStream ->
                gzipInputStream.readBytes().toString(Charsets.UTF_8)
            }
        }.getOrDefault(content)
    }

    fun isCompressedText(content: String): Boolean {
        return content.startsWith(COMPRESSED_TEXT_PREFIX)
    }

    private fun compressText(content: String): String {
        val outputStream = ByteArrayOutputStream()
        GZIPOutputStream(outputStream).use { gzipOutputStream ->
            gzipOutputStream.write(content.toByteArray(Charsets.UTF_8))
        }
        val encodedContent = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        return COMPRESSED_TEXT_PREFIX + encodedContent
    }
}
