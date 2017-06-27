package com.biryanistudio.todo.database

import io.realm.Realm

/**
 * Created by aakaashjois.
 * 27/06/17 - 4:10 PM.
 */

object TransactionsHelper {

    fun addItem(item: TodoItem) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                realm.insertOrUpdate(item)
            }
        }
    }

    fun updateItemFromId(id: Long) {
        if (id != -1L)
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction {
                    realm.where(TodoItem::class.java)
                            .equalTo(TodoItem.TIMESTAMP, id)
                            .findFirst().apply {
                        when (completed) {
                            0 -> completed = 1
                            1 -> completed = 0
                        }
                    }
                }
            }

    }

    fun deleteItemFromId(id: Long?) {
        if (id != null && id != -1L)
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction {
                    realm.where(TodoItem::class.java)
                            .equalTo(TodoItem.TIMESTAMP, id)
                            .findFirst()
                            .deleteFromRealm()
                }
            }
    }
}