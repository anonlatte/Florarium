package com.anonlatte.florarium.ui.settings.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Base class for all view holders in the app.
 */
abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T)
}