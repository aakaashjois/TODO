package com.biryanistudio.todo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.biryanistudio.todo.R
import com.biryanistudio.todo.adapters.TodoAdapter
import com.biryanistudio.todo.database.TodoItem
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.empty_view_holder.view.*
import kotlinx.android.synthetic.main.list_fragment.view.*

/**
 * Created by Aakaash Jois.
 * 23/06/17 - 9:21 AM.
 */

class TodoFragment : Fragment() {

    var page: Int? = null

    companion object {

        val PAGE = "page"

        fun newInstance(page: Int): TodoFragment {
            return TodoFragment().apply {
                arguments = Bundle().apply { putFloat(PAGE, page.toFloat()) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = arguments.getFloat(PAGE, -1f).toInt()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        //TODO: Replace Realm with Realm extensions
        val realm = Realm.getDefaultInstance()
        val realmResults = realm.where(TodoItem::class.java)
                ?.equalTo(TodoItem.COMPLETED, page)
                ?.findAllSorted(TodoItem.TIMESTAMP, Sort.DESCENDING)
        return inflater.inflate(R.layout.list_fragment, container, false).apply {
            when (page) {
                0 -> empty_view.text = context.getString(R.string.text_not_added_pending_yet)
                1 -> empty_view.text = context.getString(R.string.text_not_completed)
            }
            recycler_view.setAdapter(TodoAdapter(activity, realmResults, true, false))
        }
    }
}
