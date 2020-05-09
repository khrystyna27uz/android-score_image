package com.imagescore.utils.file

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns

class FileUtil {

    companion object {

        fun getFileName(uri: Uri?, context: Context): String? {
            var result: String? = null
            if (uri?.scheme == "content") {
                val cursor: Cursor = context.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
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

        fun getSize(context: Context, uri: Uri?): String? {
            var fileSize: String? = null
            val cursor = context.contentResolver
                .query(uri, null, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {

                    // get file size
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (!cursor.isNull(sizeIndex)) {
                        fileSize = cursor.getString(sizeIndex)
                    }
                }
            } finally {
                cursor!!.close()
            }
            return fileSize
        }

    }
}