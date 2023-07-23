package com.anonlatte.florarium.extensions

import java.util.Locale

object StringExt {

    fun String.capitalizeFirstLetter(): String {
        return replaceFirstChar {
            if (it.isLowerCase()) {
                it.titlecase(Locale.getDefault())
            } else {
                it.toString()
            }
        }
    }
}