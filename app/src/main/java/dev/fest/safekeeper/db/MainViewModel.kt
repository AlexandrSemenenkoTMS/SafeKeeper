package dev.fest.safekeeper.db

import androidx.lifecycle.*
import dev.fest.safekeeper.entities.PasswordItem
import kotlinx.coroutines.launch

class MainViewModel(dataBase: MainDataBase) : ViewModel() {

    private var dao = dataBase.getDao()

    val allPasswords: LiveData<List<PasswordItem>> = dao.getAllPasswordItems().asLiveData()

    fun insertPasswordItem(passwordItem: PasswordItem) = viewModelScope.launch {
        dao.insertPasswordItem(passwordItem)
    }

    fun editPasswordItem(passwordItem: PasswordItem) = viewModelScope.launch {
        dao.editPasswordItem(passwordItem)
    }

    fun deletePasswordItemById(passwordItem: PasswordItem) = viewModelScope.launch {
        dao.deletePasswordItemById(passwordItem)
    }

    class MainViewModelFactory(private val dataBase: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(dataBase) as T
            }
            throw  IllegalAccessException("Unknown ViewModelClass")
        }
    }
}