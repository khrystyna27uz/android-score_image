package com.imagescore.ui.adapter

/**
 * Obtained from article from DORFMAN:
 * http://hannesdorfmann.com/android/adapter-delegates
 */

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @param <T> the type of adapters data source i.e. List<Accessory>
</Accessory></T> */
abstract class BasicAdapterDelegate<in T>(val context: Context) {

    abstract val viewType: Int

    /**
     * Creates the  [RecyclerView.ViewHolder] for the given data source item
     *
     * @param parent The ViewGroup parent of the given datasource
     *
     * @return The new instantiated [RecyclerView.ViewHolder]
     */
    abstract fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    /**
     * Called to bind the [RecyclerView.ViewHolder] to the item of the datas source set
     *
     * @param items The data source
     *
     * @param holder The [RecyclerView.ViewHolder] to bind
     */
    abstract fun onBindViewHolder(item: T, holder: RecyclerView.ViewHolder)
}
