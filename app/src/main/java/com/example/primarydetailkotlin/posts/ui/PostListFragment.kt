package com.example.primarydetailkotlin.posts.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.primarydetailkotlin.R
import com.example.primarydetailkotlin.databinding.PostListFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment responsible for displaying the list of posts.
 *
 * This fragment manages the RecyclerView, the SelectionTracker for multi-select,
 * and interactions with the [PostViewModel]. It also handles the Contextual Action Bar (CAB)
 * when items are selected.
 */
@AndroidEntryPoint
class PostListFragment : Fragment() {

    private lateinit var mAdapter: PostListAdapter

    // ActionMode for the Contextual Action Bar (CAB) when items are selected.
    private var mActionMode: ActionMode? = null

    private lateinit var mSelectionTracker: SelectionTracker<Long>

    private val viewModel: PostViewModel by viewModels()

    private var _binding: PostListFragmentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PostListFragmentBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup the options menu (Top Bar) using MenuProvider API.
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.settings -> {
                        findNavController().navigate(resId = R.id.action_postListFragment_to_settingsFragment)
                    }

                    else -> return false
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Initialize the RecyclerView adapter with the read-marking callback.
        mAdapter = PostListAdapter(markRead = { long -> markRead(postId = long) })

        // Configure the RecyclerView with LayoutManager and Divider.
        binding.postList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        // Initialize the SelectionTracker for managing multi-selection.
        // It uses Stable IDs (Long) from the adapter.
        mSelectionTracker = SelectionTracker.Builder(
            "selection",
            binding.postList,
            RecyclerViewIdKeyProvider(recyclerView = binding.postList),
            PostLookup(recyclerView = binding.postList),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        // Inject the tracker into the adapter so ViewHolders can update their state.
        mAdapter.mTracker = mSelectionTracker

        // Observer for selection changes to manage the Contextual Action Bar (CAB).
        mSelectionTracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    val count: Int = mSelectionTracker.selection.size()
                    if (count == 0) {
                        // Close the CAB if no items are selected.
                        mActionMode?.finish()
                        mActionMode = null
                    } else {
                        // Start the CAB if not already active.
                        if (mActionMode == null) {
                            mActionMode =
                                (activity as AppCompatActivity).startSupportActionMode(
                                    actionModeCallback
                                )
                        }
                        // Update the title with the number of selected items.
                        mActionMode?.title =
                            resources.getQuantityString(R.plurals.count_selected, count, count)
                    }
                }
            })

        // Fetch posts from the server (if database is empty).
        viewModel.serverPosts()

        // Collect the stream of posts from the ViewModel and submit to the adapter.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.posts.collectLatest {
                    mAdapter.submitList(it)
                }
            }
        }
    }

    /**
     * Helper to mark a list of posts as read.
     *
     * @param postIds A list of IDs of posts to mark as read.
     */
    fun markRead(postIds: List<Long>) {
        viewModel.markRead(postIds = postIds)
    }

    /**
     * Helper to mark a single post as read.
     *
     * @param postId The ID of the post to mark as read.
     */
    private fun markRead(postId: Long) {
        viewModel.markRead(postId = postId)
    }

    /**
     * Helper to delete a list of posts.
     *
     * @param postIds A list of IDs of posts to delete.
     */
    fun deletePosts(postIds: List<Long>) {
        viewModel.deletePosts(postIds = postIds)
    }

    /**
     * Retrieves the currently selected item IDs from the tracker.
     *
     * @return A list of selected post IDs.
     */
    private fun getSelection(): List<Long> {
        val selection = mSelectionTracker.selection
        return selection.map { it }
    }

    // Callback for the Support Action Mode (CAB).
    private val actionModeCallback = object : ActionMode.Callback {

        // Inflate the CAB menu options.
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.action, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return true
        }

        // Handle clicks on CAB menu items (Delete, Mark as Read).
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.delete -> {
                    deletePosts(postIds = getSelection())
                }

                R.id.markRead -> {
                    markRead(postIds = getSelection())
                }
            }
            // Finish the mode after the action is performed.
            mode?.finish()
            return true
        }

        // Clean up when the CAB is closed (e.g. by back press or finish()).
        override fun onDestroyActionMode(mode: ActionMode?) {
            mActionMode = null
            mSelectionTracker.clearSelection()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

/**
 * KeyProvider that maps RecyclerView positions to stable item IDs (Long).
 * Used by SelectionTracker to maintain selection across configuration changes.
 */
class RecyclerViewIdKeyProvider(private val recyclerView: RecyclerView) :
    ItemKeyProvider<Long>(SCOPE_MAPPED) {

    override fun getKey(position: Int): Long {
        return recyclerView.adapter?.getItemId(position)
            ?: throw IllegalStateException("RecyclerView adapter is not set!")
    }

    override fun getPosition(key: Long): Int {
        val viewHolder = recyclerView.findViewHolderForItemId(key)
        return viewHolder?.layoutPosition ?: RecyclerView.NO_POSITION
    }
}
