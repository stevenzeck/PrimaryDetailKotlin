package com.example.primarydetailkotlin.posts.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.primarydetailkotlin.R
import com.example.primarydetailkotlin.databinding.PostDetailItemBinding
import com.example.primarydetailkotlin.posts.domain.model.Post
import com.example.primarydetailkotlin.posts.ui.PostListAdapter.Companion.POST_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment responsible for displaying the details of a single post.
 *
 * This fragment retrieves the post ID from arguments, fetches the post data
 * via the ViewModel, and displays it. It also allows deleting the post.
 */
@AndroidEntryPoint
class PostDetailFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels()
    private var post: Post? = null

    private var _binding: PostDetailItemBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout using View Binding.
        _binding = PostDetailItemBinding.inflate(
            LayoutInflater.from(context)
        )

        // Retrieve the Post ID from the arguments bundle and fetch the post details.
        arguments?.let {
            if (it.containsKey(POST_ID)) {
                lifecycleScope.launch {
                    // Fetch post asynchronously from the database.
                    post = viewModel.postById(it.getLong(POST_ID))

                    // Bind data to UI elements.
                    binding.titleTextView.text = post?.title
                    binding.bodyTextView.text = post?.body
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup the options menu for the detail view (Delete action).
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.delete -> {
                        // Delete the current post and navigate back.
                        post?.let { viewModel.deletePost(it.id) }
                        findNavController().navigateUp()
                    }

                    else -> return false
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
