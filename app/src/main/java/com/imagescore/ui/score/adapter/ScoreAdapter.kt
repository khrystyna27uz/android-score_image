package com.imagescore.ui.score.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.imagescore.ui.score.model.ImageScoreModel

class ScoreAdapter(
    callback: ScoreCallback,
    context: Context
) : ListAdapter<ImageScoreModel, ScoreDelegateManager.ScoreViewHolder>(ScoreDiffUtils()) {

    companion object {
        const val VIEW_ITEM = 0
    }
    private val manager =
        ScoreDelegateManager()
            .add(ScoreItemDelegate(context, callback))

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScoreDelegateManager.ScoreViewHolder {
        return manager.onCreateViewHolder(parent, viewType)!!
    }

    override fun onBindViewHolder(
        holder: ScoreDelegateManager.ScoreViewHolder,
        position: Int
    ) {
        manager.onBindViewHolder(getItem(position), holder)
    }

    override fun getItemViewType(position: Int): Int {
        return manager.getItemViewType()
    }

    interface ScoreCallback {
        fun details(imageScoreModel: ImageScoreModel)
        fun score(imageScoreModel: ImageScoreModel, score: Int)
    }
}
