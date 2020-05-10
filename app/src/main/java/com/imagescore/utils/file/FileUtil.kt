package com.imagescore.utils.file

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.imagescore.domain.ui.score.model.FileFormat
import java.io.File

const val URI_SCHEME = "content"

class FileUtil {

    companion object {

        // Get height in pixels from image Uri
        fun getImageHeight(uri: Uri, context: Context): Long {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(
                context.contentResolver.openInputStream(uri),
                null,
                options
            )
            val imageHeight = options.outHeight
            return imageHeight.toLong()
        }

        // Get width in pixels from image Uri
        fun getImageWidth(uri: Uri, context: Context): Long {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(
                context.contentResolver.openInputStream(uri),
                null,
                options
            )
            val imageWidth = options.outWidth
            return imageWidth.toLong()
        }

        fun generatePhotoUri(context: Context): Uri {
            val dir = File(context.filesDir, "images")
            dir.mkdirs()
            val fileName = "${System.currentTimeMillis()}.jpg"
            val file = File(dir, fileName)
            val authority = "${context.packageName}.provider"
            return FileProvider.getUriForFile(context, authority, file)
        }

        // Get name for image from it's Uri
        fun getFileName(uri: Uri?, context: Context): String? {
            var result: String? = null
            if (uri?.scheme == URI_SCHEME) {
                val cursor: Cursor = context.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        val strings: List<String> = result.split(".")
                        if (strings.isNotEmpty()) {
                            result = strings[0]
                        }

                    }
                } finally {
                    cursor.close()
                }
            }
            if (result == null) {
                result = uri?.path
                val cut = result?.lastIndexOf('/')
                if (cut != -1) {
                    result = cut?.plus(1)?.let { result?.substring(it) }
                }
            }
            return result
        }


        fun getImageFormat(uri: Uri): FileFormat {
            val path = uri.path
            return when (path.substring(path.lastIndexOf("."))) {
                "png" -> FileFormat.PNG
                "jpg" -> FileFormat.JPEG
                else -> FileFormat.JPEG
            }

        }

        // Get image size in Bites
        fun getSize(context: Context, uri: Uri?): Long {
            var fileSize: Long = 0
            val cursor = context.contentResolver
                .query(uri, null, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {

                    // get file size
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (!cursor.isNull(sizeIndex)) {
                        fileSize = cursor.getString(sizeIndex).toLong()
                    }
                }
            } finally {
                cursor!!.close()
            }
            return fileSize
        }

        fun getImageDate(uri: Uri, context: Context): Long {
            return getFileName(uri, context)?.toLong()!!
        }

    }
}