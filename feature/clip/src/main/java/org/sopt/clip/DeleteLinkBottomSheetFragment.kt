package org.sopt.clip

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import org.sopt.clip.databinding.FragmentDeleteLinkBottomSheetBinding
import org.sopt.ui.base.BindingBottomSheetDialogFragment
import org.sopt.ui.view.onThrottleClick

class DeleteLinkBottomSheetFragment() :
  BindingBottomSheetDialogFragment<FragmentDeleteLinkBottomSheetBinding>({ FragmentDeleteLinkBottomSheetBinding.inflate(it) }) {
  var clipId: Long? = null
  var isFullClipSize: Boolean? = null
  private var handleDelete: () -> Unit = {}
  private var handleChange: () -> Unit = {}
  private var handleModify: () -> Unit = {}
  private var isClipListEmptySnackBar: () -> Unit = {}

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    clipId = arguments?.getLong("clipId")
    isFullClipSize = arguments?.getBoolean("isFullClipSize")
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    if (clipId?.toInt() == 0) {
      binding.tvDeleteLinkChange.isVisible = false
      binding.testCoach.isVisible = false
    }

    binding.ivDeleteLinkBottomSheetClose.setOnClickListener {
      dismiss()
    }
    binding.tvDeleteLinkDelete.onThrottleClick {
      handleDelete.invoke()
      dismiss()
    }

    binding.tvDeleteLinkChange.onThrottleClick {
      if (isFullClipSize == true) {
        handleChange.invoke()
      } else {
        isClipListEmptySnackBar.invoke()
      }
      dismiss()
    }

    binding.tvDeleteLinkModify.onThrottleClick {
      handleModify.invoke()
      dismiss()
    }
  }

  companion object {
    fun newInstance(
      clipId: Long,
      isFullClipSize: Boolean,
      isClipListEmpty: () -> Unit,
      handleDeleteButton: () -> Unit,
      handleChangeButton: () -> Unit,
      handleModifyButton: () -> Unit,
    ): DeleteLinkBottomSheetFragment {
      val args = Bundle().apply {
        putLong("clipId", clipId)
        putBoolean("isFullClipSize", isFullClipSize)
      }
      return DeleteLinkBottomSheetFragment().apply {
        arguments = args
        handleDelete = handleDeleteButton
        handleChange = handleChangeButton
        handleModify = handleModifyButton
        isClipListEmptySnackBar = isClipListEmpty
      }
    }
  }
}
