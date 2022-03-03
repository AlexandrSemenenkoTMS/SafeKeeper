package dev.fest.safekeeper.utilities

import androidx.recyclerview.widget.RecyclerView
import dev.fest.safekeeper.entities.PasswordItem

interface ItemTouchCallback {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder?)
    fun onItemDelete(passwordItem: PasswordItem)
    fun onCancelItemDelete(passwordItem: PasswordItem)
}