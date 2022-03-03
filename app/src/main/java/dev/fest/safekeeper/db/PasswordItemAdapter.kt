package dev.fest.safekeeper.db

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dev.fest.safekeeper.activities.PasswordItemActivity
import dev.fest.safekeeper.databinding.PasswordListItemBinding
import dev.fest.safekeeper.entities.PasswordItem
import dev.fest.safekeeper.utilities.ItemTouchCallback
import dev.fest.safekeeper.utilities.ItemTouchHelperAdapter
import java.util.*
import kotlin.collections.ArrayList

class PasswordItemAdapter(
    private val listener: Listener,
    private val activity: PasswordItemActivity,
    private val itemTouchCallback: ItemTouchCallback
) :
    RecyclerView.Adapter<PasswordItemAdapter.PasswordItemHolder>(), Filterable,
    ItemTouchHelperAdapter {

    var passwordItemList: ArrayList<PasswordItem> = ArrayList()
    var passwordItemFilterList: ArrayList<PasswordItem> = ArrayList()

    init {
        passwordItemFilterList = passwordItemList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordItemHolder {
        val binding =
            PasswordListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PasswordItemHolder(binding, itemTouchCallback)
    }

    override fun onBindViewHolder(holder: PasswordItemHolder, position: Int) {
        holder.setData(passwordItemFilterList[position], listener, activity)
//        holder.container.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale)
    }

    override fun getItemCount(): Int = passwordItemFilterList.size

    class PasswordItemHolder(
        private val binding: PasswordListItemBinding,
        private val itemTouchCallback: ItemTouchCallback,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun setData(
            passwordItem: PasswordItem,
            listener: Listener,
            activity: PasswordItemActivity
        ) =
            with(binding) {
                tvTitle.text = passwordItem.title
                tvUserName.text = passwordItem.userName
                tvPassword.text = passwordItem.userPassword
                itemView.setOnLongClickListener {
                    copyPassword(activity)
                    return@setOnLongClickListener true
                }
                itemView.setOnClickListener {
                    listener.editItem(passwordItem)
                }
                imageButtonDragItem.setOnTouchListener { _, event ->
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        itemTouchCallback.onStartDrag(this@PasswordItemHolder)
                    }
                    false
                }
            }

        private fun copyPassword(activity: PasswordItemActivity) {
            val myClipboard =
                activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val myClip: ClipData = ClipData.newPlainText("Label", binding.tvPassword.text)
            myClipboard.setPrimaryClip(myClip)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(list: List<PasswordItem>) {
        passwordItemFilterList.clear()
        passwordItemFilterList.addAll(list)
        notifyDataSetChanged()
    }

    interface Listener {
        fun editItem(passwordItem: PasswordItem)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) passwordItemFilterList = passwordItemList else {
                    val filteredList = ArrayList<PasswordItem>()
                    passwordItemList
                        .filter {
                            (it.title?.lowercase()
                                ?.contains(constraint.toString().lowercase())!!) ||
                                    (it.userName?.lowercase()
                                        ?.contains(constraint.toString().lowercase())!!)
                        }
                        .forEach { filteredList.add(it) }
                    passwordItemFilterList = filteredList

                    Log.e("performFiltering: t1: ", filteredList.size.toString())

                }
                return FilterResults().apply { values = passwordItemFilterList }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                passwordItemFilterList = results?.values as ArrayList<PasswordItem>
                notifyDataSetChanged()
            }
        }
    }

    override fun onItemMove(startPosition: Int, targetPosition: Int) {
        Collections.swap(passwordItemFilterList, startPosition, targetPosition)
        notifyItemMoved(startPosition, targetPosition)
//        this@PasswordItemAdapter.updateAdapter(passwordItemFilterList)
//        updateCallback.onUpdateClose(passwordItemFilterList)
    }

    override fun onItemDismiss(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val alertDialog = AlertDialog.Builder(viewHolder.itemView.context)
        alertDialog.apply {
            setMessage("Do you want delete this item?")
            setPositiveButton("Delete") { _, _ ->
                val positionItem = viewHolder.adapterPosition
                val removedItem = passwordItemFilterList[positionItem]
                passwordItemFilterList.removeAt(positionItem)
                notifyItemRemoved(positionItem)
                this@PasswordItemAdapter.itemTouchCallback.onItemDelete(removedItem)
                Snackbar.make(
                    viewHolder.itemView,
                    "${removedItem?.title!!} deleted",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("UNDO") {
                        this@PasswordItemAdapter.itemTouchCallback.onCancelItemDelete(removedItem)
                        passwordItemFilterList.add(positionItem, removedItem)
                        notifyItemInserted(positionItem)
                    }.show()
            }
            setNegativeButton("Cancel") { _, _ ->
                notifyItemChanged(viewHolder.adapterPosition)
            }
            create()
            show()
        }
    }
}