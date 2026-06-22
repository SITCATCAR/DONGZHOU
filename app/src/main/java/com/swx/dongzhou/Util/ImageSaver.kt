package com.swx.dongzhou.Util

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageSaver {
    private const val IMAGE_FOLDER = "DONGZHOU_QR"
    private const val FILENAME_PREFIX = "QR_"

    fun saveToGallery(context: Context, bitmap: Bitmap): SaveResult {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveViaMediaStore(context, bitmap)
        } else {
            saveViaExternalStorage(context, bitmap)
        }
    }

    private fun saveViaMediaStore(context: Context, bitmap: Bitmap): SaveResult {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, generateFileName())
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/$IMAGE_FOLDER")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: return SaveResult.Error("Failed to create image")
        return try {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
            SaveResult.Success
        } catch (e: Exception) {
            SaveResult.Error(e.message ?: "Save failed")
        }
    }

    private fun saveViaExternalStorage(context: Context, bitmap: Bitmap): SaveResult {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PermissionChecker.PERMISSION_GRANTED
        ) {
            return SaveResult.PermissionRequired
        }
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            IMAGE_FOLDER
        )
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder, generateFileName())
        return try {
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
                data = android.net.Uri.fromFile(file)
            })
            SaveResult.Success
        } catch (e: Exception) {
            SaveResult.Error(e.message ?: "Save failed")
        }
    }

    fun getShareIntent(context: Context, bitmap: Bitmap): Intent? {
        return try {
            val shareDir = File(context.cacheDir, "share").apply {
                mkdirs()
            }
            val file = File(shareDir, "qr_share.png")
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun generateFileName(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "$FILENAME_PREFIX$timestamp.png"
    }

    sealed class SaveResult {
        object Success : SaveResult()
        object PermissionRequired : SaveResult()
        data class Error(val message: String) : SaveResult()
    }
}
