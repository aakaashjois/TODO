package com.biryanistudio.todo.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.biryanistudio.todo.R
import com.biryanistudio.todo.adapters.TodoAdapter
import com.biryanistudio.todo.database.TodoItem
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.list_fragment.view.*

/**
 * Created by aakaashjois on 23/06/17
 */

class TodoFragment : Fragment() {

    var page: Int? = null

    companion object {
        fun newInstance(page: Int): TodoFragment {
            return TodoFragment().apply {
                arguments = Bundle().apply { putFloat("page", page.toFloat()) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = arguments.getFloat("page", -1f).toInt()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                              savedInstanceState: Bundle?): View {
        val realm = Realm.getDefaultInstance()
        val realmResults = realm
                .where(TodoItem::class.java)
                .equalTo("completed", page)
                .findAllSorted("timeStamp", Sort.DESCENDING)
        return inflater.inflate(R.layout.list_fragment, container, false).apply {
            recycler_view.setAdapter(TodoAdapter(context, realmResults, true, true))
        }
    }
}
