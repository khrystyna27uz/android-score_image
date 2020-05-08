package com.imagescore.ui.score.adapter

import androidx.recyclerview.widget.DiffUtil
import com.imagescore.ui.score.model.ImageScoreModel

class ScoreDiffUtils : DiffUtil.ItemCallback<ImageScoreModel>() {
    override fun areItemsTheSame(oldItem: ImageScoreModel, newItem: ImageScoreModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ImageScoreModel, newItem: ImageScoreModel): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: ImageScoreModel, newItem: ImageScoreModel): Any? {
        return ""
    }
}