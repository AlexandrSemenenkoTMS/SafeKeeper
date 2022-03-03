package dev.fest.safekeeper.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.fest.safekeeper.MainApp
import dev.fest.safekeeper.constants.Constants
import dev.fest.safekeeper.databinding.ActivityPasswordItemBinding
import dev.fest.safekeeper.db.MainViewModel
import dev.fest.safekeeper.db.PasswordItemAdapter
import dev.fest.safekeeper.entities.PasswordItem
import dev.fest.safekeeper.utilities.ItemTouchCallback
import dev.fest.safekeeper.utilities.ItemTouchMoveCallback
import dev.fest.safekeeper.utilities.UpdateInterface

class PasswordItemActivity() : AppCompatActivity(), PasswordItemAdapter.Listener, ItemTouchCallback
     {

    private lateinit var binding: ActivityPasswordItemBinding
    private lateinit var adapter: PasswordItemAdapter
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((this.applicationContext as MainApp).database)
    }
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private var mainItemTouchHelper: ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()
        onEditResult()
        onClickNew()
        filterTitlePasswordItem()
        observer()
    }

    private fun initRecyclerView() = with(binding) {
        containerPasswordList.layoutManager = LinearLayoutManager(this@PasswordItemActivity)
        adapter = PasswordItemAdapter(
            this@PasswordItemActivity,
            this@PasswordItemActivity,
            this@PasswordItemActivity
        )
        containerPasswordList.adapter = adapter
        val callback: ItemTouchHelper.Callback = ItemTouchMoveCallback(adapter)
        mainItemTouchHelper = ItemTouchHelper(callback)
        mainItemTouchHelper?.attachToRecyclerView(binding.containerPasswordList)
    }

    private fun onEditResult() {
        editLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val editState = it.data?.getStringExtra(Constants.EDIT_STATE_KEY)
                    if (editState == "update") {
                        mainViewModel.editPasswordItem(
                            it.data?.getSerializableExtra(
                                Constants.NEW_PASSWORD_KEY
                            ) as PasswordItem
                        )
                    } else {
                        mainViewModel.insertPasswordItem(
                            it.data?.getSerializableExtra(
                                Constants.NEW_PASSWORD_KEY
                            ) as PasswordItem
                        )
                    }
                }
            }
    }

    private fun filterTitlePasswordItem() {
        binding.passwordItemSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun observer() {
        mainViewModel.allPasswords.observe(this) {
            adapter.updateAdapter(it)
            binding.textViewEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun onClickNew() {
        binding.buttonAddNew.setOnClickListener {
            editLauncher.launch(Intent(this, NewPasswordItemActivity::class.java))
        }
    }

    override fun editItem(passwordItem: PasswordItem) {
        val intent = Intent(this, NewPasswordItemActivity::class.java).apply {
            putExtra(Constants.NEW_PASSWORD_KEY, passwordItem)
        }
        editLauncher.launch(intent)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        viewHolder?.let { mainItemTouchHelper?.startDrag(viewHolder) }
    }

    override fun onItemDelete(passwordItem: PasswordItem) {
        mainViewModel.deletePasswordItemById(passwordItem)
    }

    override fun onCancelItemDelete(passwordItem: PasswordItem) {
        mainViewModel.insertPasswordItem(passwordItem)
    }

}