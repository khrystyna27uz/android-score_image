package com.imagescore.ui.score.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.imagescore.R
import com.imagescore.ui.adapter.BasicAdapterDelegate
import com.imagescore.ui.score.model.ImageScoreModel
import kotlinx.android.synthetic.main.item_score.view.*

class ScoreItemDelegate(
    context: Context,
    val callback: ScoreAdapter.ScoreCallback
) : BasicAdapterDelegate<Any>(context) {

    class AllServicesHolder(itemView: View) :
        ScoreDelegateManager.ScoreViewHolder(itemView)

    override val viewType: Int
        get() = ScoreAdapter.VIEW_ITEM

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_score, parent, false)
        return AllServicesHolder(
            view
        )
    }

    override fun onBindViewHolder(item: Any, holder: RecyclerView.ViewHolder) {
        val model = item as ImageScoreModel
        holder.itemView.serviceNameTV.text = model.title
        holder.itemView.ratingBar.rating = model.score.toFloat()
        Glide.with(context)
            .load(model.imagePath)
            .placeholder(R.drawable.no_photo)
            .into(holder.itemView.serviceIV)
        holder.itemView.ratingBar.onRatingBarChangeListener =
            OnRatingBarChangeListener { ratingBar, _, _ ->
                callback.score(model, ratingBar.rating.toInt())
            }
        holder.itemView.serviceIV.setOnClickListener {
            callback.details(model)
        }
    }
}