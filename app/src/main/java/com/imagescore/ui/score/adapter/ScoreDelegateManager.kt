package com.imagescore.ui.score.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imagescore.ui.adapter.BasicAdapterDelegate

class ScoreDelegateManager {

    internal var delegates: ArrayList<BasicAdapterDelegate<Any>> = ArrayList()

    abstract class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun add(delegate: BasicAdapterDelegate<Any>) = this.apply { delegates.add(delegate) }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder? {
        return delegates
            .firstOrNull { it.viewType == viewType }
            ?.let { it.onCreateViewHolder(parent) as ScoreViewHolder }
    }

    fun onBindViewHolder(items: Any, holder: ScoreViewHolder) {
        delegates
            .filter { isCorrespondent(it, holder) }
            .forEach { it.onBindViewHolder(items, holder) }
    }

    private fun isCorrespondent(
        delegate: BasicAdapterDelegate<Any>,
        holder: ScoreViewHolder
    ): Boolean {
        return delegate.viewType == holder.itemViewType
    }

    fun getItemViewType(): Int {
        return  ScoreAdapter.VIEW_ITEM
    }
}