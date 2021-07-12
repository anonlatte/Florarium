package com.anonlatte.florarium.adapters

import android.widget.ImageView
import com.anonlatte.florarium.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

fun ImageView.load(imageUrl: String?) {
    Glide.with(context)
        .load(imageUrl)
        .error(R.drawable.flower_example)
        .transition(DrawableTransitionOptions.withCrossFade())
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}
