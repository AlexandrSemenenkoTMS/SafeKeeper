package dev.fest.safekeeper.db

import androidx.room.*
import androidx.room.Dao
import dev.fest.safekeeper.entities.PasswordItem
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    //password_items
    @Query("select * from password_item")
    fun getAllPasswordItems(): Flow<List<PasswordItem>>

    @Insert
    suspend fun insertPasswordItem(passwordItem: PasswordItem)

    @Update
    suspend fun editPasswordItem(passwordItem: PasswordItem)

    @Delete
    suspend fun deletePasswordItemById(passwordItem: PasswordItem)

    @Query("SELECT * FROM password_item WHERE title LIKE :title")
    suspend fun getAllPasswordItems(title: String): List<PasswordItem>



}