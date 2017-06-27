package com.biryanistudio.todo.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.biryanistudio.todo.database.TransactionsHelper

/**
 * Created by Aakaash Jois.
 * 27/06/17 - 5:40 PM.
 */
class TouchHelperCallback(val context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TransactionsHelper.updateItemFromId(viewHolder.itemId)
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?,
                        target: RecyclerView.ViewHolder?): Boolean = true

//    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView,
//                             viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
//                             actionState: Int, isCurrentlyActive: Boolean) {
//        val itemView = viewHolder.itemView
//        val height: Float = (itemView.bottom - itemView.top).toFloat()
//        val width: Float = height / 3
//        val paint: Paint = Paint()
//        if (dX > 0) {
//            paint.color = Color.WHITE
//            val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
//            c.drawRect(background, paint)
//            val icon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_check_circle)
//            val iconDest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width,
//                    itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
//            c.drawBitmap(icon, null, iconDest, paint)
//        }
//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//    }
}