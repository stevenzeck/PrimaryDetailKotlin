package com.example.primarydetailkotlin.posts.ui

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.primarydetailkotlin.R
import com.example.primarydetailkotlin.databinding.PostListItemBinding
import com.example.primarydetailkotlin.posts.domain.model.Post

/**
 * RecyclerView Adapter for displaying the list of posts.
 *
 * This adapter handles the binding of [Post] objects to views, selection state management,
 * and navigation when an item is clicked. It uses [ListAdapter] to efficiently handle list updates.
 *
 * @property markRead A callback function invoked when a post is clicked to mark it as read.
 */
class PostListAdapter(private val markRead: (Long) -> Unit) :
    ListAdapter<Post, PostListAdapter.ViewHolder>(PostListDiff()) {

    /**
     * The tracker used to manage item selection (e.g., long-press multi-select).
     * It is set after the adapter is created and attached to the RecyclerView.
     */
    var mTracker: SelectionTracker<Long>? = null

    /**
     * Keeps track of the currently selected position for single-click navigation/highlighting purposes.
     * Note: This is separate from the [SelectionTracker] which handles multi-selection mode.
     */
    var mSelected: Int = RecyclerView.NO_POSITION

    init {
        // Stable IDs are required for the SelectionTracker to work correctly with the adapter.
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            PostListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    /**
     * Returns the unique stable ID for the item at the given position.
     * We use the Post ID as the stable ID.
     */
    override fun getItemId(position: Int): Long = getItem(position).id

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        // Pass the selection state from the tracker to the ViewHolder to update the UI (e.g., background color).
        val isSelected = mTracker?.isSelected(item.id) ?: false
        holder.bind(post = item, isActivated = isSelected)

        // Set the tag to the item itself, which can be useful for debugging or retrieval.
        with(holder.itemView) {
            tag = item
        }
    }

    /**
     * ViewHolder class for post items.
     */
    inner class ViewHolder(
        private val binding: PostListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the post data to the view and sets up click listeners.
         *
         * @param post The post to display.
         * @param isActivated Whether the item is currently selected in the multi-select mode.
         */
        fun bind(post: Post, isActivated: Boolean) {
            // Update the activated state for the view selector (controls background highlighting).
            itemView.isActivated = isActivated

            // Update the selected state based on single-selection tracking.
            itemView.isSelected = absoluteAdapterPosition == mSelected

            binding.postTitle.text = post.title

            // Bold the title if the post is unread.
            val typeface = if (post.read) Typeface.NORMAL else Typeface.BOLD
            binding.postTitle.setTypeface(null, typeface)

            // Handle item clicks
            binding.root.setOnClickListener {
                // If we are in selection mode, let the tracker handle it.
                // We shouldn't navigate when items are being selected.
                if (mTracker?.hasSelection() == true) {
                    return@setOnClickListener
                }

                // Update selection state for single-select highlighting
                notifyItemChanged(mSelected)
                mSelected = absoluteAdapterPosition
                notifyItemChanged(mSelected)

                // Invoke callback to mark as read
                markRead(post.id)

                val bundle = bundleOf(POST_ID to post.id)

                // Check if we are in a two-pane layout (e.g., tablet) by looking for the detail container.
                val itemDetailFragmentContainer: View? =
                    binding.root.rootView.findViewById(R.id.post_detail_container)

                if (itemDetailFragmentContainer != null) {
                    // Tablet: Update the detail pane
                    itemDetailFragmentContainer.findNavController()
                        .navigate(resId = R.id.postDetailFragmentPane, args = bundle)
                } else {
                    // Phone: Navigate to the detail screen
                    itemView.findNavController()
                        .navigate(
                            resId = R.id.action_postListFragment_to_postDetailFragment,
                            args = bundle
                        )
                }
            }
        }

        /**
         * Returns the details of the item for the Selection library.
         */
        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getSelectionKey(): Long = itemId
                override fun getPosition(): Int = absoluteAdapterPosition
            }
    }

    companion object {
        const val POST_ID = "postId"
    }
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by [ListAdapter] to calculate the minimum number of changes between an old list and a new
 * list that's been passed to `submitList`.
 */
internal class PostListDiff : DiffUtil.ItemCallback<Post>() {

    /**
     * Checks if two items represent the same object.
     */
    override fun areItemsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * Checks if the content of two items is the same.
     * This determines if the item needs to be redrawn.
     */
    override fun areContentsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem.title == newItem.title
                && oldItem.read == newItem.read
    }
}
