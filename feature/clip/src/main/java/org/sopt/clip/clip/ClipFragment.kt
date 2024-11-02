package org.sopt.clip.clip

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import designsystem.components.bottomsheet.BottomSheetType
import designsystem.components.bottomsheet.LinkMindBottomSheet
import designsystem.components.toast.linkMindSnackBar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.sopt.clip.R
import org.sopt.clip.databinding.FragmentClipBinding
import org.sopt.ui.base.BindingFragment
import org.sopt.ui.fragment.viewLifeCycle
import org.sopt.ui.fragment.viewLifeCycleScope
import org.sopt.ui.nav.DeepLinkUtil
import org.sopt.ui.view.UiState
import org.sopt.ui.view.onThrottleClick

@AndroidEntryPoint
class ClipFragment : BindingFragment<FragmentClipBinding>({ FragmentClipBinding.inflate(it) }) {
  private val viewModel: ClipViewModel by viewModels()
  private lateinit var clipAdapter: ClipAdapter
  val bundle = Bundle()
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initClipAdapter()
    viewModel.getCategoryAll()
    updateClipList()
    updateAllClipCount()
    onClickSearchButton()
    onClickEditButton()
    onClickAddButton()
    isCheckClipCount()
    collectAddCategory()
  }

  private fun updateClipList() {
    viewModel.categoryState.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          if (state.data.isEmpty()) {
            binding.ivClipEmpty.visibility = View.VISIBLE
            binding.tvClipEmpty.visibility = View.VISIBLE
          }
          clipAdapter.submitList(state.data)
          setEmptyMsgVisible()
        }

        else -> {}
      }
    }.launchIn(viewLifeCycleScope)
  }

  private fun updateAllClipCount() {
    viewModel.allClipCount.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          binding.tvClipAllCount.text = "(${state.data})"
        }

        else -> {}
      }
    }.launchIn(viewLifeCycleScope)
  }

  private fun collectAddCategory() {
    viewModel.duplicateState.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          if (!state.data.isDuplicate) {
            requireContext().linkMindSnackBar(binding.vSnack, "클립 생성 완료!", false)
            viewModel.initializeDuplicateState()
          } else {
            requireContext().linkMindSnackBar(binding.vSnack, "이미 같은 이름의 클립이 있습니다.", false)
            viewModel.initializeDuplicateState()
          }
        }
        else -> {}
      }
    }.launchIn(viewLifeCycleScope)
  }

  private fun isCheckClipCount() {
    viewModel.test.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          if (!state.data) requireContext().linkMindSnackBar(binding.vSnack, "클립 개수는 15개까지 가능합니다", true)
        }
        else -> {
        }
      }
    }.launchIn(viewLifeCycleScope)
  }

  private fun initClipAdapter() {
    clipAdapter = ClipAdapter { category ->
      val action = ClipFragmentDirections.actionNavigationClipToNavigationClipLink(category.categoryId ?: 0, category.categoryTitle ?: "전체 클립")
      findNavController().navigate(action)
    }
    binding.rvClipClip.adapter = clipAdapter
    binding.rvClipClip.itemAnimator = null
  }

  private fun setEmptyMsgVisible() {
    binding.ivClipEmpty.visibility = View.GONE
    binding.tvClipEmpty.visibility = View.GONE
  }

  private fun onClickAddButton() {
    binding.btnClipAdd.onThrottleClick {
      val addClipBottomSheet = LinkMindBottomSheet(requireContext())
      addClipBottomSheet.show()
      addClipBottomSheet.apply {
        setBottomSheetType(BottomSheetType.CLIP)
        setBottomSheetHint(org.sopt.mainfeature.R.string.clip_new_clip_info)
        setTitle(org.sopt.mainfeature.R.string.clip_add_clip)
        setErroMsg(org.sopt.mainfeature.R.string.error_clip_length)
        bottomSheetConfirmBtnClick {
          viewModel.getCategoryDuplicate(getText())
          dismiss()
        }
      }
    }
  }

  private fun onClickEditButton() {
    binding.btnClipEdit.onThrottleClick {
      findNavController().navigate(R.id.action_navigation_clip_to_navigation_clip_edit)
    }
  }

  private fun onClickSearchButton() {
    binding.clClipSearch.onThrottleClick {
      navigateToDestination("featureMyPage://fragmentSearch")
    }
  }

  private fun navigateToDestination(destination: String) {
    val (request, navOptions) = DeepLinkUtil.getNavRequestNotPopUpAndOption(
      destination,
      enterAnim = org.sopt.mainfeature.R.anim.from_bottom,
      exitAnim = android.R.anim.fade_out,
      popEnterAnim = android.R.anim.fade_in,
      popExitAnim = org.sopt.mainfeature.R.anim.to_bottom,
    )
    findNavController().navigate(request, navOptions)
  }
}
