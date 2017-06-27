package com.biryanistudio.todo.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Aakaash Jois.
 * 23/06/17 - 9:21 AM.
 */
open class TodoItem(var task: String = "", @PrimaryKey var timestamp: Long = 0,
                    var completed: Int = 0) : RealmObject() {
    companion object {
        val TIMESTAMP = "timestamp"
        val COMPLETED = "completed"
    }
}