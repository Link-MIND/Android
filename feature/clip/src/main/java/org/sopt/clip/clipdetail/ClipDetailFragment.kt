package org.sopt.clip.clipdetail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.sopt.clip.ClipViewModel
import org.sopt.clip.LinkDTO
import org.sopt.clip.SelectedToggle
import org.sopt.clip.databinding.FragmentClipDetailBinding
import org.sopt.ui.base.BindingFragment
import org.sopt.ui.view.onThrottleClick

@AndroidEntryPoint
class ClipDetailFragment : BindingFragment<FragmentClipDetailBinding>({ FragmentClipDetailBinding.inflate(it) }) {
  private val viewModel by viewModels<ClipViewModel>()
  private lateinit var clipDetailAdapter: ClipLinkAdapter

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    clipDetailAdapter = ClipLinkAdapter()
    binding.rvCategoryLink.adapter = clipDetailAdapter
    updateListView()

    initToggleClickListener()
    onClickBackButton()
  }

  private fun onClickBackButton() {
    binding.ivClipDetailBack.onThrottleClick {
      viewModel.navigateBack(findNavController())
    }
  }


  private fun updateListView() {
    viewModel.mockDataListState.observe( viewLifecycleOwner
    ) {
      initEmptyMsgVisible(it)
      if (!it) {
        clipDetailAdapter.submitList(viewModel.mockLinkData)
      }
    }
  }

  private fun initToggleClickListener(): List<LinkDTO> {
    with(binding) {
      btnClipAll.setOnClickListener {
        updateTogglesNDividerVisible(SelectedToggle.ALL)
      }

      btnClipRead.setOnClickListener {
        updateTogglesNDividerVisible(SelectedToggle.READ)
      }

      btnClipUnread.setOnClickListener {
        updateTogglesNDividerVisible(SelectedToggle.UNREAD)
      }
    }
    return viewModel.mockLinkData
  }

  private fun updateTogglesNDividerVisible(selectedNow: SelectedToggle) {
    updateTogglesVisible(selectedNow)
    initDividerVisible(selectedNow)
  }


  private fun updateTogglesVisible(selectedNow: SelectedToggle) {
    if (selectedNow != viewModel.toggleSelectedPast) {
      initToggleVisible(viewModel.toggleSelectedPast, false)
      initToggleVisible(selectedNow, true)
      initDividerVisible(selectedNow)
      viewModel.toggleSelectedPast = selectedNow
      updateListView()
    }
  }

  private fun initToggleVisible(toggle: SelectedToggle, state: Boolean) {
    with(binding) {
      when (toggle) {
        SelectedToggle.ALL -> tvClipAllSelected.isVisible = state
        SelectedToggle.READ -> tvClipReadSelected.isVisible = state
        SelectedToggle.UNREAD -> tvClipUnreadSelected.isVisible = state
      }
    }
  }

  private fun initDividerVisible(selectedNow: SelectedToggle) {
    with(binding) {
      when (selectedNow) {
        SelectedToggle.ALL -> {
          dvClipPicker1.isVisible = false
          dvClipPicker2.isVisible = true
        }

        SelectedToggle.READ -> {
          dvClipPicker1.isVisible = false
          dvClipPicker2.isVisible = false
        }

        SelectedToggle.UNREAD -> {
          dvClipPicker1.isVisible = true
          dvClipPicker2.isVisible = false
        }
      }
    }
  }

  private fun initEmptyMsgVisible(state: Boolean) {
    with(binding) {
      ivClipCategoryEmpty.isVisible = state
      tvClipDetailEmpty.isVisible = state
    }
  }
}
