package com.biryanistudio.todo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.biryanistudio.todo.R
import com.biryanistudio.todo.adapters.TodoRecyclerViewAdapter
import com.biryanistudio.todo.adapters.TouchHelperCallback
import com.biryanistudio.todo.database.TodoItem
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.list_fragment.view.*
import kotlin.properties.Delegates.notNull

/**
 * Created by Aakaash Jois.
 * 23/06/17 - 9:21 AM.
 */

class TodoFragment : Fragment() {

    var page: Int by notNull()
    var realm: Realm by notNull()

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        realm = Realm.getDefaultInstance()
        val realmResults = realm.where(TodoItem::class.java).equalTo(TodoItem.COMPLETED, page)
                .findAllSorted(TodoItem.TIMESTAMP, Sort.DESCENDING)
        val itemTouchHelper = ItemTouchHelper(TouchHelperCallback(context))
        val view = inflater.inflate(R.layout.list_fragment, container, false)
        with(view) {
            recycler_view.adapter = TodoRecyclerViewAdapter(activity, realmResults)
            recycler_view.setHasFixedSize(true)
            itemTouchHelper.attachToRecyclerView(recycler_view)
            empty_view.text = when (page) {
                0 -> context.getString(R.string.text_not_added_pending_yet)
                1 -> context.getString(R.string.text_not_completed)
                else -> null
            }
            if (realmResults.size == 0) {
                recycler_view.visibility = View.GONE
                empty_view.visibility = View.VISIBLE
            } else {
                recycler_view.visibility = View.VISIBLE
                empty_view.visibility = View.GONE
            }
            realmResults.addChangeListener({ result ->
                if (result.size == 0) {
                    empty_view.visibility = View.VISIBLE
                    recycler_view.visibility = View.GONE
                } else {
                    empty_view.visibility = View.GONE
                    recycler_view.visibility = View.VISIBLE
                }
            })
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
