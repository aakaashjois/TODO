package com.biryanistudio.todo.userinterface

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo

import com.biryanistudio.todo.R
import com.biryanistudio.todo.database.TodoItem
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_new_task_dialog.*

class NewTaskDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dialog(this, R.style.NewTaskDialog).apply {
            setContentView(R.layout.activity_new_task_dialog)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            setOnCancelListener { finish() }
            setOnShowListener {
                dialog_task_input.requestFocus()
                dialog_task_input.setOnEditorActionListener { textView, i, _ ->
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        val text = textView.text.toString().trim { it <= ' ' }
                        Realm.getDefaultInstance().executeTransaction {
                            it.createObject(TodoItem::class.java).apply {
                                completed = false
                                task = text
                                timestamp = System.currentTimeMillis()
                            }
                            it.close()
                        }
                        this.dismiss()
                        UiUtils.createNotification(this@NewTaskDialogActivity, text)
                        finish()
                    }
                    true
                }
            }
        }.show()
    }
}
