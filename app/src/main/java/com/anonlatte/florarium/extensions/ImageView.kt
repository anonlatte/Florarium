package com.anonlatte.florarium.extensions

import android.net.Uri
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

fun ImageView.load(imageUrl: Uri?) {
    Glide.with(context)
        .load(imageUrl)
        .error(R.drawable.flower_example)
        .transition(DrawableTransitionOptions.withCrossFade())
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}
