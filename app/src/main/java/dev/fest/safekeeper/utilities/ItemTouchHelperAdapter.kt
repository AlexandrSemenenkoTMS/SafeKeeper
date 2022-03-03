package dev.fest.safekeeper.utilities

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperAdapter {
    fun onItemMove(startPosition: Int, targetPosition: Int)
    fun onItemDismiss(viewHolder: RecyclerView.ViewHolder, position: Int)
}