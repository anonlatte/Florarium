package com.anonlatte.florarium.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T> Flow<T>.launchWhenStarted(
    lifecycleScope: LifecycleCoroutineScope,
) {
    lifecycleScope.launchWhenStarted { collect() }
}

fun <T> Flow<T>.collectWithLifecycle(lifecycleOwner: LifecycleOwner, collector: (T) -> Unit) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect(collector)
        }
    }
}
