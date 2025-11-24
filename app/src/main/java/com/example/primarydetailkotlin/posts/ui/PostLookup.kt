package com.example.primarydetailkotlin.posts.ui

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

/**
 * Implementation of [ItemDetailsLookup] for the RecyclerView selection library.
 *
 * This class allows the selection library to determine which item in the RecyclerView
 * is under the user's touch, enabling features like gesture selection and mouse support.
 *
 * @property recyclerView The RecyclerView instance this lookup is attached to.
 */
class PostLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {

    /**
     * Translates a motion event coordinate to the details of the item under that coordinate.
     *
     * @param event The [MotionEvent] representing the user interaction (touch/click).
     * @return The [ItemDetails] of the item under the touch point, or null if no item is there.
     */
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as PostListAdapter.ViewHolder)
                .getItemDetails()
        }
        return null
    }

}
