package com.anonlatte.florarium.ui.home.adapters

import androidx.recyclerview.selection.ItemDetailsLookup

interface ViewHolderWithDetails<TItem> {
    fun getItemDetail(): ItemDetailsLookup.ItemDetails<TItem>
}
