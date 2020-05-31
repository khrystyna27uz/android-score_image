@file:Suppress("Recycle")

package com.imagescore.utils.file

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Size
import androidx.core.content.FileProvider
import com.imagescore.domain.ui.score.model.FileFormat
import java.io.File

object FileUtil {
    fun generatePhotoUri(context: Context): PhotoUri {
        val dir = File(context.filesDir, "images")
        dir.mkdirs()
        val fileName = "${System.currentTimeMillis()}.jpg"
        val file = File(dir, fileName)
        val authority = "${context.packageName}.provider"
        return PhotoUri(
            uri = FileProvider.getUriForFile(context, authority, file),
            format = FileFormat.JPEG
        )
    }

    fun getFileSize(context: Context, uri: Uri): Long {
        var fileSize = -1L
        val cursor = context.contentResolver
            .query(uri, null, null, null, null) ?: return -1L
        cursor.use {
            cursor.moveToFirst()

            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            fileSize = cursor.getLong(sizeIndex)
        }
        return fileSize
    }

    fun getImageSize(context: Context, uri: Uri): Size {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(
            context.contentResolver.openInputStream(uri),
            null,
            options
        )
        return Size(options.outWidth, options.outHeight)
    }
}

data class PhotoUri(
    val uri: Uri,
    val format: FileFormat
)