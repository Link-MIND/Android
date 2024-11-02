package org.sopt.savelink.ui.savelinksetclip

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import designsystem.components.bottomsheet.BottomSheetType
import designsystem.components.bottomsheet.LinkMindBottomSheet
import designsystem.components.button.state.LinkMindButtonState
import designsystem.components.dialog.LinkMindDialog
import designsystem.components.toast.linkMindSnackBar
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.viewmodel.observe
import org.sopt.mainfeature.R
import org.sopt.savelink.databinding.FragmentSaveLinkSetClipBinding
import org.sopt.savelink.ui.adapter.ClipSelectAdapter
import org.sopt.savelink.ui.model.Clip
import org.sopt.ui.base.BindingFragment
import org.sopt.ui.nav.DeepLinkUtil
import org.sopt.ui.view.onThrottleClick

@AndroidEntryPoint
class SaveLinkSetClipFragment : BindingFragment<FragmentSaveLinkSetClipBinding>({ FragmentSaveLinkSetClipBinding.inflate(it) }) {

  private val viewModel: SetLinkViewModel by viewModels()
  private lateinit var adapter: ClipSelectAdapter
  private val linkMindDialog by lazy {
    LinkMindDialog(requireContext())
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initView()
    collectState()
    onClickAddClip()
    onClickNavigateUp()
    onCLickNavigateCloseDialog()
    onClickCompleteBtn()
  }

  private fun initView() {
    binding.btnSaveLinkComplete.state = LinkMindButtonState.ENABLE
    viewModel.getCategoryAll()
    val clipboardLink = arguments?.getString("clipboardLink")
    viewModel.updateUrl(clipboardLink ?: "")
  }

  private fun collectState() {
    viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleSideEffect)
  }

  private fun render(homeState: SaveLinkSetClipState) {
    initSetClipAdapter(homeState.categoryList)
    binding.tvSaveLinkClipCount.text = "전체 (${homeState.allClipCountNum})"
    adapter.submitList(homeState.categoryList)
    if (homeState.duplicate) {
      requireContext().linkMindSnackBar(binding.vSnack, "이미 같은 이름의 클립이 있습니다.", false)
      viewModel.updateDuplicate()
    }
  }

  private fun handleSideEffect(sideEffect: SaveLinkSetClipSideEffect) {
    when (sideEffect) {
      is SaveLinkSetClipSideEffect.NavigateSaveLinkSetClip -> {
        navigateToHome()
        requireContext().linkMindSnackBar(binding.vSnack, "링크 저장 완료", false)
      }

      is SaveLinkSetClipSideEffect.NavigateUp -> findNavController().navigateUp()
      is SaveLinkSetClipSideEffect.ShowBottomSheet -> showAddClipBottomSheet()
      is SaveLinkSetClipSideEffect.ShowDialog -> showCloseDialog()
      is SaveLinkSetClipSideEffect.ShowSnackBar -> requireContext().linkMindSnackBar(binding.btnSaveLinkComplete, "유효하지 않은 링크입니다.", false)
      is SaveLinkSetClipSideEffect.ShowSnackBarError -> requireContext().linkMindSnackBar(binding.btnSaveLinkComplete, "클립 개수는 15개까지 가능합니다", false)
    }
  }

  private fun initSetClipAdapter(list: List<Clip>) {
    adapter = ClipSelectAdapter(
      onClickClip = { clip, position ->
        if (clip.isSelected) {
          list.onEach { it.isSelected = false }
          list[position].isSelected = true
          viewModel.updateCategoryId(clip.categoryId)
          binding.btnSaveLinkComplete.state = LinkMindButtonState.ENABLE
        } else {
          list.onEach { it.isSelected = false }
        }
      },
    )
    binding.rvItemTimerClipSelect.adapter = adapter
  }

  private fun onClickAddClip() {
    binding.tvSaveLinkAddClip.onThrottleClick {
      viewModel.showBottomSheet()
    }
  }

  private fun onClickNavigateUp() {
    binding.ivSaveLinkClipBack.onThrottleClick {
      viewModel.navigateUp()
    }
  }

  private fun onCLickNavigateCloseDialog() {
    binding.ivSaveLinkClose.onThrottleClick {
      viewModel.showDialog()
    }
  }

  private fun onClickCompleteBtn() {
    binding.btnSaveLinkComplete.apply {
      btnClick {
        if (state == LinkMindButtonState.DISABLE) return@btnClick
        viewModel.viewModelScope.launch {
          viewModel.saveLink(viewModel.container.stateFlow.value.url, viewModel.container.stateFlow.value.categoryId)
        }
      }
    }
  }

  private fun showCloseDialog() {
    linkMindDialog.setTitle(R.string.save_clip_dialog_title)
      .setSubtitle(R.string.save_clip_dialog_sub_title)
      .setNegativeButton(R.string.negative_close_msg) {
        linkMindDialog.dismiss()
        navigateToHome()
      }
      .setPositiveButton(R.string.negative_close_cancel) {
        linkMindDialog.dismiss()
      }
      .show()
  }

  private fun showAddClipBottomSheet() {
    val linkMindBottomSheet = LinkMindBottomSheet(requireContext())
    linkMindBottomSheet.show()
    linkMindBottomSheet.apply {
      setBottomSheetType(BottomSheetType.CLIP)
      setTitle(R.string.clip_add_clip)
      setErroMsg(R.string.error_clip_length)
      setBottomSheetHint(R.string.home_new_clip_info)
      bottomSheetConfirmBtnClick {
        viewModel.getCategoryDuplicate(it)
        if (showErrorMsg(BottomSheetType.CLIP)) return@bottomSheetConfirmBtnClick
        dismiss()
      }
    }
  }

  private fun navigateToHome() {
    val (request, navOptions) = DeepLinkUtil.getNavRequestPopUpAndAnimption(
      findNavController().graph.id,
      false,
      "featureHome://homeFragment",
      enterAnim = R.anim.from_bottom,
      exitAnim = android.R.anim.fade_out,
      popEnterAnim = android.R.anim.fade_in,
      popExitAnim = R.anim.to_bottom,
    )
    findNavController().navigate(request, navOptions)
  }
}
