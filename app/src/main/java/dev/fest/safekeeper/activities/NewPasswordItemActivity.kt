package dev.fest.safekeeper.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.fest.safekeeper.R
import dev.fest.safekeeper.constants.Constants.CREATE_DATA
import dev.fest.safekeeper.constants.Constants.EDIT_STATE_KEY
import dev.fest.safekeeper.constants.Constants.NEW_PASSWORD_KEY
import dev.fest.safekeeper.constants.Constants.UPDATE_DATA
import dev.fest.safekeeper.databinding.ActivityNewPasswordBinding
import dev.fest.safekeeper.entities.PasswordItem

class NewPasswordItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewPasswordBinding

    private var passwordItem: PasswordItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getPasswordItem()
        showPassword()
        copyPassword()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_password_item_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_password_item) {
            checkEditText()
        } else if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkEditText() {
        binding.apply {
            when {
                editTextTitle.text.toString().trim().isEmpty() -> {
                    editTextTitle.error = getString(R.string.new_password_activity_error)
                }
                editTextUserName.text.toString().trim().isEmpty() -> {
                    editTextUserName.error = getString(R.string.new_password_activity_error)
                }
                editTextPassword.text.toString().trim().isEmpty() -> {
                    editTextPassword.error = getString(R.string.new_password_activity_error)
                }
                else -> {
                    setMainResult()
                }
            }
        }
    }

    private fun setMainResult() {
        var editState = CREATE
        val tempNote: PasswordItem? = if (passwordItem == null) {
            createNewPasswordItem()
        } else {
            editState = UPDATE
            updatePasswordItem()
        }
        val intent = Intent().apply {
            putExtra(NEW_PASSWORD_KEY, tempNote)
            putExtra(EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK, intent)
        finish()
        createNewPasswordItem()
        finish()
        Toast.makeText(this, "save item", Toast.LENGTH_LONG).show()
    }

    private fun setActionBarSettings(state: String) {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        if (state == CREATE_DATA) {
            actionBar?.title = getString(R.string.new_password_activity_create_data)
        } else if (state == UPDATE_DATA) {
            actionBar?.title = getString(R.string.new_password_activity_update_data)
        }
    }

    private fun getPasswordItem() {
        val serializablePasswordItem =
            intent.getSerializableExtra(NEW_PASSWORD_KEY)
        if (serializablePasswordItem != null) {
            passwordItem = serializablePasswordItem as PasswordItem
            fillPasswordItem()
            setActionBarSettings(UPDATE_DATA)
        } else {
            setActionBarSettings(CREATE_DATA)
        }
    }

    private fun fillPasswordItem() = with(binding) {
        if (passwordItem != null) {
            editTextTitle.setText(passwordItem?.title)
            editTextUserName.setText(passwordItem?.userName)
            editTextPassword.setText(passwordItem?.userPassword)
            editTextDescription.setText(passwordItem?.description)
        }
    }

    private fun createNewPasswordItem(): PasswordItem = with(binding) {
        return PasswordItem(
            null,
            editTextTitle.text.toString(),
            editTextUserName.text.toString(),
            editTextPassword.text.toString(),
            editTextDescription.text.toString()
        )
    }

    private fun updatePasswordItem(): PasswordItem? = with(binding) {
        return passwordItem?.copy(
            title = editTextTitle.text.toString(),
            userName = editTextUserName.text.toString(),
            userPassword = editTextPassword.text.toString(),
            description = editTextDescription.text.toString()
        )
    }

    private fun showPassword() = with(binding) {
        visibilityPassword.setOnClickListener {
            val resource = if (editTextPassword.transformationMethod != null) {
                editTextPassword.transformationMethod = null
                R.drawable.ic_visibility_off
            } else {
                editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                R.drawable.ic_visibility
            }
            editTextPassword.setSelection(editTextPassword.text?.length!!)
            visibilityPassword.setImageResource(resource)
        }
    }

    private fun copyPassword() = with(binding) {
        copyPassword.setOnClickListener {
            val myClipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val myClip: ClipData = ClipData.newPlainText("Label", editTextPassword.text)
            myClipboard.setPrimaryClip(myClip)
        }
    }

    companion object{
        const val CREATE = "CREATE"
        const val UPDATE = "UPDATE"
    }
}