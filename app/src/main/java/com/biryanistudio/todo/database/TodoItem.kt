package com.biryanistudio.todo.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Aakaash Jois.
 * 23/06/17 - 9:21 AM.
 */
open class TodoItem(
        @PrimaryKey var id: String = "",
        var task: String = "",
        var timestamp: Long = 0,
        var completed: Int = 0
) : RealmObject() {
    companion object {
        val ID = "id"
        val TIMESTAMP = "timestamp"
        val COMPLETED = "completed"
    }
}