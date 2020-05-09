package com.imagescore.ui.details.view

import com.imagescore.mvp.BasicView
import com.imagescore.ui.score.model.ImageScoreModel

interface DetailsView : BasicView {
    fun setUpUI()
    fun setScoreData(imageScore: ImageScoreModel)
    fun goToEditTitleDialog(currentTitle: String)
}