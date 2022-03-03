package dev.fest.safekeeper.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "password_item")
data class PasswordItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "userName")
    val userName: String?,
    @ColumnInfo(name = "userPassword")
    val userPassword: String?,
    @ColumnInfo(name = "description")
    val description: String?,
) : Serializable
