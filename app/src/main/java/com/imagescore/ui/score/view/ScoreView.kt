package com.imagescore.ui.score.view

import android.graphics.Bitmap
import com.imagescore.mvp.BasicView
import com.imagescore.ui.score.model.ImageScoreModel

interface ScoreView : BasicView {
    fun setScoreData(data: List<ImageScoreModel>)
    fun makeRequestWritePermissions(requestCode: Int)
    fun openCamera()
    fun makeRequestCamera()
    fun setUpPhotoError()
    fun setUpUI()
    fun setUpPhoto(photo : Bitmap)
    fun onPermissionDenied()
}