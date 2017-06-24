package com.biryanistudio.todo

import android.app.Application
import io.realm.Realm

/**
 * Created by aakaashjois on 23/06/17
 */

class TodoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}