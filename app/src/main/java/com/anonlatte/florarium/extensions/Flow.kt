package com.anonlatte.florarium.extensions

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

fun <T> Flow<T>.launchWhenStarted(
    lifecycleScope: LifecycleCoroutineScope,
) {
    lifecycleScope.launchWhenStarted { collect() }
}