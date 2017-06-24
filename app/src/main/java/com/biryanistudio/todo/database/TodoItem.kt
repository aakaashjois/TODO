package com.biryanistudio.todo.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by aakaashjois on 23/06/17
 */
open class TodoItem(
        @PrimaryKey var id: Int = 0,
        var task: String = "",
        var timestamp: Long = 0,
        var completed: Int = 0
) : RealmObject() {
    companion object {
        val ID = "id"
        val TASK = "task"
        val TIMESTAMP = "timestamp"
        val COMPLETED = "completed"
    }
}