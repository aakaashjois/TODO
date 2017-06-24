package com.biryanistudio.todo.services

import android.app.Service
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.biryanistudio.todo.TodoApplication
import com.biryanistudio.todo.database.TodoItem
import io.realm.Realm

class CopyListenerService : Service(), ClipboardManager.OnPrimaryClipChangedListener {
    private var clipboardManager: ClipboardManager? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager?.addPrimaryClipChangedListener(this)
        return Service.START_STICKY
    }

    override fun onPrimaryClipChanged() {
        val clipData = clipboardManager?.primaryClip
        clipboardManager?.removePrimaryClipChangedListener(this)
        if (clipData?.description?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) as Boolean) {
            var clipText = clipData.getItemAt(0).text.toString()
            if (clipText.contains("//TODO")
                    || clipText.contains("// TODO")
                    || clipText.contains("//todo")
                    || clipText.contains("// todo")
                    || clipText.contains("//Todo")
                    || clipText.contains("// Todo")) {
                clipText = if (clipText.contains("//TODO"))
                    clipText.replace("//TODO", "")
                else if (clipText.contains("// TODO"))
                    clipText.replace("// TODO", "")
                else if (clipText.contains("//todo"))
                    clipText.replace("//todo", "")
                else if (clipText.contains("// todo"))
                    clipText.replace("// todo", "")
                else if (clipText.contains("//Todo"))
                    clipText.replace("//Todo", "")
                else
                    clipText.replace("// Todo", "")
                clipText = clipText.trim { it <= ' ' }
                if (!clipText.trim { it <= ' ' }.isEmpty()) {
                    saveTextToDatabase(clipText)
                }
            }
        }
    }

    private fun saveTextToDatabase(text: String) {
        Realm.getDefaultInstance().executeTransaction {
            it.createObject(TodoItem::class.java).apply {
                completed = 0
                task = text
                timestamp = System.currentTimeMillis()
            }
            it.close()
        }
        TodoApplication.createNotification(this, text)
        clipboardManager?.addPrimaryClipChangedListener(this)
    }
}