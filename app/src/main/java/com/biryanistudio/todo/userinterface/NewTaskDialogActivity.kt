package com.biryanistudio.todo.userinterface

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import com.biryanistudio.todo.R
import com.biryanistudio.todo.TodoApplication
import com.biryanistudio.todo.database.TodoItem
import com.vicpin.krealmextensions.save
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
                        TodoItem().apply {
                            completed = 0
                            task = text
                            timestamp = System.currentTimeMillis()
                        }.save()
                        this.dismiss()
                        TodoApplication.createNotification(this@NewTaskDialogActivity, text)
                        finish()
                    }
                    true
                }
            }
        }.show()
    }
}
