package dev.fest.safekeeper.utilities

import dev.fest.safekeeper.entities.PasswordItem

interface UpdateInterface {
    fun onUpdateClose(list: ArrayList<PasswordItem>)
}