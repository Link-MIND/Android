package org.sopt.timer.settimer.clipselect

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import designsystem.components.button.state.LinkMindButtonState
import designsystem.components.dialog.LinkMindDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.sopt.model.timer.Clip
import org.sopt.timer.R
import org.sopt.timer.databinding.FragmentTimerClipSelectBinding
import org.sopt.timer.settimer.SetTimerViewModel
import org.sopt.ui.base.BindingFragment
import org.sopt.ui.fragment.viewLifeCycle
import org.sopt.ui.fragment.viewLifeCycleScope
import org.sopt.ui.view.UiState
import org.sopt.ui.view.onThrottleClick

@AndroidEntryPoint
class TimerClipSelectFragment : BindingFragment<FragmentTimerClipSelectBinding>({ FragmentTimerClipSelectBinding.inflate(it) }) {
  private lateinit var adapter: ClipSelectAdapter

  private val viewModel: SetTimerViewModel by activityViewModels()
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    getCategoryAll()
    collectClipState()
    initCloseButtonClickListener()
  }

  private fun getCategoryAll() {
    if (viewModel.clipState.value !is UiState.Success) {
      viewModel.getCategoryAll()
    }
  }

  private fun collectClipState() {
    viewModel.clipState.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          initClipSelectAdapter(state.data)
          initNextButtonClickListener(state.data)
        }

        else -> {}
      }
    }.launchIn(viewLifeCycleScope)
  }

  private fun initClipSelectAdapter(list: List<Clip>) {
    adapter = ClipSelectAdapter(
      onClick = { clip, index ->
        handleClipClick(clip, list, index)
      },
      context = requireContext(),
    )
    adapter.selectedPosition = list.indexOfFirst { it.isSelected }
    adapter.submitList(list)
    binding.btnTimerClipSelectNext.state = if (adapter.selectedPosition != -1) LinkMindButtonState.ENABLE else LinkMindButtonState.DISABLE
    binding.rvItemTimerClipSelect.adapter = adapter
    binding.rvItemTimerClipSelect.itemAnimator = null
  }

  private fun handleClipClick(
    clip: Clip,
    list: List<Clip>,
    index: Int,
  ) {
    if (clip.isSelected) {
      list.onEach { it.isSelected = false }
      list[index].isSelected = true
      binding.btnTimerClipSelectNext.state = LinkMindButtonState.ENABLE
    } else {
      list.onEach { it.isSelected = false }
      binding.btnTimerClipSelectNext.state = LinkMindButtonState.DISABLE
    }
  }

  private fun initNextButtonClickListener(list: List<Clip>) {
    binding.btnTimerClipSelectNext.btnClick {
      viewModel.setClipList(list)
      findNavController().navigate(R.id.action_navigation_timer_clip_select_to_navigation_time_picker)
    }
  }

  private fun initCloseButtonClickListener() {
    binding.ivTimerClipSelectClose.onThrottleClick {
      val linkMindDialog = LinkMindDialog(requireContext())
      linkMindDialog.setTitle(org.sopt.mainfeature.R.string.timer_cancel_dialog_title)
      linkMindDialog.setSubtitle(org.sopt.mainfeature.R.string.timer_cancel_dialog_sub_title)
      linkMindDialog.setPositiveButton(org.sopt.mainfeature.R.string.positive_ok_msg) {
        findNavController().navigateUp()
        linkMindDialog.dismiss()
      }
      linkMindDialog.setNegativeButton(org.sopt.mainfeature.R.string.negative_close_cancel) {
        linkMindDialog.dismiss()
      }
      linkMindDialog.show()
    }
  }
}
