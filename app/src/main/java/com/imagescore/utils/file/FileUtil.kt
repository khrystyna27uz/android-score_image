package com.imagescore.utils.file

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import java.io.ByteArrayOutputStream


const val PICTURE_SIZE=400

class FileUtil {

    companion object {
        fun getRealPathFromURI(context: Context, contentURI: Uri): String {
            var result = ""
            val cursor = context.contentResolver.query(contentURI, null, null, null, null)
            cursor?.let {
                it.moveToFirst()
                val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                result = it.getString(idx)
                cursor.close()
            } ?: run {
                result = contentURI.path
            }
            return result
        }

        fun imageToBase(imagePath: String?): String? {
            return imagePath?.let {
                try {
                    val bm = decodeBitmapPath(imagePath, PICTURE_SIZE, PICTURE_SIZE)
                    val baos = ByteArrayOutputStream()
                    bm?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val byteArrayImage = baos.toByteArray()
                    Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                } catch (e: IllegalStateException) {
                    null
                }

            }
        }

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


        fun decodeBitmapPath(path: String, width: Int,
                             height: Int): Bitmap? {

            val ourOption = BitmapFactory.Options()
            ourOption.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, ourOption)
            ourOption.inSampleSize = calculateInSampleSize(ourOption, width,
                height)
            // Decode bitmap with inSampleSize set
            ourOption.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(path, ourOption)?.let { it }
        }

        private fun calculateInSampleSize(ourOption: BitmapFactory.Options,
                                          imageWidth: Int, imageHeight: Int): Int {
            val height = ourOption.outHeight
            val width = ourOption.outWidth
            var inSampleSize = 1
            if (height > imageHeight || width > imageWidth) {
                if (width > height) {
                    inSampleSize = Math.round(height.toFloat() / imageHeight.toFloat())
                } else {
                    inSampleSize = Math.round(width.toFloat() / imageWidth.toFloat())
                }
            }
            return inSampleSize
        }


        fun getImageUri(context: Context, inImage: Bitmap): Uri {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
            return Uri.parse(path)
        }

    }
}