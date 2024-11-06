package org.sopt.clip.clipedit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import designsystem.components.bottomsheet.LinkMindBottomSheet
import designsystem.components.dialog.LinkMindDialog
import designsystem.components.toast.linkMindSnackBar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.sopt.clip.databinding.FragmentClipEditBinding
import org.sopt.mainfeature.R
import org.sopt.ui.base.BindingFragment
import org.sopt.ui.fragment.viewLifeCycle
import org.sopt.ui.fragment.viewLifeCycleScope
import org.sopt.ui.view.UiState
import org.sopt.ui.view.onThrottleClick

@AndroidEntryPoint
class ClipEditFragment : BindingFragment<FragmentClipEditBinding>({ FragmentClipEditBinding.inflate(it) }) {
  private val viewModel: ClipEditViewModel by viewModels()
  private lateinit var clipEditAdapter: ClipEditAdapter

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    clipEditAdapter = ClipEditAdapter { itemId, state, position, title ->
      when (state) {
        "delete" -> {
          showDeleteDialog(itemId, title)
        }

        "edit" -> {
          showHomeBottomSheet(itemId, title)
        }
      }
    }
    binding.rvClipEdit.adapter = clipEditAdapter
    val callback = ItemTouchCallback(
      adapter = clipEditAdapter,
      recyclerView = binding.rvClipEdit,
    ) { categoryId, newIndex ->
      if (categoryId != null) viewModel.updateCategoryEditPriorityState(categoryId, newIndex)
    }
    ItemTouchHelper(callback).attachToRecyclerView(binding.rvClipEdit)
    updateEditListView()
    updateDelete()
    onClickBackButton()
    editCategoryPriority()
  }

  private fun updateEditListView() {
    viewModel.categoryState.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          val originalList = state.data ?: emptyList()
          val newList = originalList.filter { it.categoryId?.toInt() != 0 }
          clipEditAdapter.submitList(newList)
        }

        else -> {}
      }
    }.launchIn(viewLifeCycleScope)
  }

  private fun updateDelete() {
    viewModel.categoryDeleteState.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          requireContext().linkMindSnackBar(binding.vSnack, "클립 삭제 완료", false)
          viewModel.getCategoryAll()
        }

        else -> {}
      }
    }.launchIn(viewLifeCycleScope)
  }

  private fun onClickBackButton() {
    binding.ivClipEditBack.onThrottleClick {
      findNavController().navigateUp()
    }
  }

  private fun showHomeBottomSheet(itemId: Long, itemText: String) {
    val editTitleBottomSheet = LinkMindBottomSheet(requireContext())
    editTitleBottomSheet.show()
    editTitleBottomSheet.apply {
      setBottomSheetHint(org.sopt.mainfeature.R.string.home_new_clip_info)
      setTitle(org.sopt.mainfeature.R.string.edit_clip_edit_title)
      setBottomSheetText(itemText)
      bottomSheetConfirmBtnClick { // dto 수정됨
        val clipNewName = getText()
        viewModel.patchCategoryEditTitle(itemId, clipNewName)
        editCategoryTitle()
        dismiss()
      }
    }
  }

  private fun editCategoryTitle() {
    viewModel.editTitleState.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          requireContext().linkMindSnackBar(binding.vSnack, "클립 수정 완료!", false)
          viewModel.getCategoryAll()
        }

        else -> {
        }
      }
    }.launchIn(viewLifeCycleScope)
  }

  private fun showDeleteDialog(itemId: Long, title: String) {
    val deleteDialog = LinkMindDialog(requireContext())
    deleteDialog.setTitleText("'$title' 클립을 삭제하시겠어요?")
      .setSubtitle(R.string.edit_clip_delete_dialog_subtitle)
      .setNegativeButton(R.string.negative_close_msg) {
        deleteDialog.dismiss()
      }
      .setPositiveButton(R.string.edit_clip_delete_dialog_delete) {
        viewModel.deleteCategory(itemId)
        deleteDialog.dismiss()
      }
      .show()
  }

  private fun editCategoryPriority() {
    viewModel.categoryEditPriorityState.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          viewModel.patchCategoryEditPriority(state.data.first, state.data.second)
        }

        else -> {}
      }
    }.launchIn(viewLifeCycleScope)
  }
}
