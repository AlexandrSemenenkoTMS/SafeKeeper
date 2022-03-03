package dev.fest.safekeeper

import android.app.Application
import dev.fest.safekeeper.db.MainDataBase

class MainApp : Application() {
    val database by lazy { MainDataBase.getDataBase(this) }
}