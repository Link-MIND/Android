package org.sopt.clip.clipchange

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import designsystem.components.button.state.LinkMindButtonState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.sopt.clip.cliplink.ClipLinkViewModel
import org.sopt.clip.databinding.FragmentClipChangeBinding
import org.sopt.model.timer.Clip
import org.sopt.model.timer.toUiModel
import org.sopt.ui.base.BindingFragment
import org.sopt.ui.fragment.viewLifeCycle
import org.sopt.ui.fragment.viewLifeCycleScope
import org.sopt.ui.view.UiState
import org.sopt.ui.view.onThrottleClick

@AndroidEntryPoint
class ClipChangeFragment :
  BindingFragment<FragmentClipChangeBinding>({ FragmentClipChangeBinding.inflate(it) }) {
  private lateinit var clipAdapter: ClipChangeAdapter
  private val viewModel: ClipLinkViewModel by activityViewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val args: ClipChangeFragmentArgs by navArgs()
    getCategoryAll()
    collectClipState(args)
    initCloseButtonClickListener()
  }

  private fun getCategoryAll() {
    viewModel.getCategoryAll()
  }

  private fun collectClipState(args: ClipChangeFragmentArgs) {
    viewModel.categoryState.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          initClipSelectAdapter(state.data.toUiModel(), args.toastId, args.clipId)
        }

        else -> {}
      }
    }.launchIn(viewLifeCycleScope)
  }

  private fun initClipSelectAdapter(list: List<Clip>, toastId: Long, currentClipId: Long) {
    val clipList = excludeCurrentClipId(list, currentClipId)
    clipAdapter = ClipChangeAdapter(
      onClick = { clip, index ->
        handleClipClick(clip, clipList, index, clip.id, toastId)
      },
      context = requireContext(),
    )
    clipAdapter.selectedPosition = clipList.indexOfFirst { it.isSelected }
    clipAdapter.submitList(clipList)
    binding.btnClipChangeSelectNext.state = if (clipAdapter.selectedPosition != -1) LinkMindButtonState.ENABLE else LinkMindButtonState.DISABLE
    binding.rvClipChangeSelect.adapter = clipAdapter
    binding.rvClipChangeSelect.itemAnimator = null
  }

  private fun handleClipClick(
    clip: Clip,
    list: List<Clip>,
    index: Int,
    newClipId: Long,
    toastId: Long,
  ) {
    if (clip.isSelected) {
      list.onEach { it.isSelected = false }
      list[index].isSelected = true
      binding.btnClipChangeSelectNext.state = LinkMindButtonState.ENABLE
    } else {
      list.onEach { it.isSelected = false }
      binding.btnClipChangeSelectNext.state = LinkMindButtonState.DISABLE
    }

    initNextButtonClickListener(toastId, newClipId)
  }

  private fun initNextButtonClickListener(toastId: Long, newClipId: Long) {
    binding.btnClipChangeSelectNext.btnClick {
      viewModel.patchLinkCategory(toastId = toastId, categoryId = newClipId)
      findNavController().popBackStack()
    }
  }

  private fun initCloseButtonClickListener() {
    binding.ivClipChangeClose.onThrottleClick {
      findNavController().popBackStack()
    }
  }

  private fun excludeCurrentClipId(clipList: List<Clip>, currentClipId: Long): List<Clip> =
    clipList.filter { it.id != currentClipId }
}
