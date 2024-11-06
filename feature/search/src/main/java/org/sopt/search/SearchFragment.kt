package org.sopt.search

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe
import org.sopt.common.util.delSpace
import org.sopt.search.adapter.ClipResultAdapter
import org.sopt.search.adapter.LinkResultAdapter
import org.sopt.search.databinding.FragmentSearchBinding
import org.sopt.ui.base.BindingFragment
import org.sopt.ui.context.hideKeyboard
import org.sopt.ui.nav.DeepLinkUtil
import org.sopt.ui.view.onThrottleClick
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class SearchFragment : BindingFragment<FragmentSearchBinding>({ FragmentSearchBinding.inflate(it) }) {
  private val viewModel: SearchViewModel by viewModels()
  private lateinit var linkResultAdapter: LinkResultAdapter
  private lateinit var clipResultAdapter: ClipResultAdapter
  private lateinit var mResultAdapter: ConcatAdapter

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    linkResultAdapter = LinkResultAdapter {
      viewModel.navigateToWebView(it.linkUrl!!, it.toastId, it.isRead!!)
    }
    clipResultAdapter = ClipResultAdapter {
      viewModel.navigateToClip(it.categoryId!!, it.categoryTitle!!)
    }
    mResultAdapter = ConcatAdapter(linkResultAdapter, clipResultAdapter)

    binding.rcSearchResult.adapter = mResultAdapter
    setOnClickListeners()
    setDoAfterTextChangedListener()
    viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleSideEffect)
  }

  private fun render(searchState: SearchState) {
    binding.clNoneResults.visibility = if (searchState.isEmpty && searchState.query.isNotEmpty()) View.VISIBLE else View.GONE
    linkResultAdapter.submitList(searchState.searchedToasts)
    clipResultAdapter.submitList(searchState.searchedClip)
    binding.rcSearchResult.visibility = if (searchState.isEmpty) View.INVISIBLE else View.VISIBLE
    binding.ivSearch.visibility = if (searchState.query.isNotEmpty()) View.GONE else View.VISIBLE
    binding.ivCancel.visibility = if (searchState.query.isNotEmpty()) View.VISIBLE else View.GONE
    updateSearchQuery(searchState.query)
    if (searchState.query.isEmpty()) {
      viewModel.clear()
      binding.editText.setText("")
    }
    if (searchState.isEmpty) {
      linkResultAdapter.submitList(emptyList())
      clipResultAdapter.submitList(emptyList())
    }
  }

  private fun handleSideEffect(searchSideEffect: SearchSideEffect) {
    when (searchSideEffect) {
      is SearchSideEffect.NavigateToClip -> {
        navigateToDestination(
          "featureSaveLink://ClipLinkFragment/${searchSideEffect.id}/${searchSideEffect.title}",
        )
      }
      is SearchSideEffect.NavigateToWebView -> {
        naviagateToWebViewFragment(searchSideEffect.url, searchSideEffect.id, searchSideEffect.isRead)
      }
    }
  }

  private fun setDoAfterTextChangedListener() {
    binding.editText.doAfterTextChanged {
      viewModel.updateQuery(it.toString())
    }
  }

  private fun setOnClickListeners() {
    binding.ivCancel.onThrottleClick {
      viewModel.clear()

      requireContext().hideKeyboard(requireView())
    }
  }

  private fun updateSearchQuery(query: String) {
    linkResultAdapter.setSearchQuery(query)
    clipResultAdapter.setSearchQuery(query)
  }

  private fun naviagateToWebViewFragment(site: String, toastId: Long, isRead: Boolean) {
    val encodedURL = URLEncoder.encode(site, StandardCharsets.UTF_8.toString())
    navigateToDestination("featureSaveLink://webViewFragment/$toastId/$isRead/${true}/$encodedURL")
  }

  private fun navigateToDestination(destination: String) {
    val (request, navOptions) = DeepLinkUtil.getNavRequestNotPopUpAndOption(
      destination.delSpace(),
      enterAnim = org.sopt.mainfeature.R.anim.from_bottom,
      exitAnim = android.R.anim.fade_out,
      popEnterAnim = android.R.anim.fade_in,
      popExitAnim = org.sopt.mainfeature.R.anim.to_bottom,
    )
    findNavController().navigate(request, navOptions)
  }
}
