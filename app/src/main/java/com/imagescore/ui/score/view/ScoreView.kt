package com.imagescore.ui.score.view

import com.imagescore.mvp.BasicView
import com.imagescore.ui.score.model.ImageScoreModel

interface ScoreView : BasicView {
    fun setScoreData(data: List<ImageScoreModel>)
    fun openCamera()
    fun makeRequestCamera()
    fun setUpPhotoError()
    fun setUpUI()
    fun onPermissionDenied()
}