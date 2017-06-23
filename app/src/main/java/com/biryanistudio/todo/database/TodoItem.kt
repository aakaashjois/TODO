package com.biryanistudio.todo.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by aakaashjois on 23/06/17
 */
open class TodoItem(
        @PrimaryKey var id: Long = 0,
        var task: String = "",
        var timestamp: Long = 0,
        var completed: Boolean = false
) : RealmObject()