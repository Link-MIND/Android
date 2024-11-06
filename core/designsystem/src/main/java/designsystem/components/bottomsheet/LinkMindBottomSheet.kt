package designsystem.components.bottomsheet

import android.content.Context
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import designsystem.components.button.state.LinkMIndFullWidthButtonState
import designsystem.components.edittext.state.LinkMindEditTextState
import designsystem.components.toast.linkMindSnackBar
import org.sopt.mainfeature.databinding.BottomSheetDialogLinkmindBinding
import org.sopt.ui.view.onThrottleClick

class LinkMindBottomSheet(context: Context) {
  private val context: Context = context
  private val binding: BottomSheetDialogLinkmindBinding = BottomSheetDialogLinkmindBinding.inflate(LayoutInflater.from(context))
  private val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(context).apply {
    setContentView(binding.root)
  }

  init {
    binding.ivBottomSheetClose.onThrottleClick { dismiss() }
    binding.btnBottomSheet.state = LinkMIndFullWidthButtonState.DISABLE

    bottomSheetDialog.window?.let { window ->
      window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
      window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
      binding.etvBottomSheet.editText.requestFocus()
    }
  }

  fun setBottomSheetType(bottomSheetType: BottomSheetType) {
    binding.etvBottomSheet.apply {
      throttleAfterTextChanged {
        handleTextChange(bottomSheetType)
      }

      onClickTextClear {
        binding.btnBottomSheet.state = LinkMIndFullWidthButtonState.DISABLE
        binding.tvBottomSheetErrorText.isGone = true
        binding.etvBottomSheet.state = LinkMindEditTextState.ENABLE
      }
    }
  }

  fun setBottomSheetHint(@StringRes textId: Int) {
    binding.etvBottomSheet.editText.setHint(textId)
  }

  fun setBottomSheetHint(text: String) {
    binding.etvBottomSheet.editText.hint = text
  }

  fun setBottomSheetText(text: String) {
    binding.etvBottomSheet.editText.setText(text)
    binding.etvBottomSheet.setEditTextSelection()
  }

  fun bottomSheetConfirmBtnClick(onClick: (String) -> Unit) {
    binding.btnBottomSheet.btnClick {
      onClick(binding.etvBottomSheet.editText.text.toString())
    }
  }

  private fun handleTextChange(bottomSheetType: BottomSheetType) {
    val isError = showErrorMsg(bottomSheetType)
    binding.apply {
      tvBottomSheetErrorText.isVisible = isError
      if (isError) binding.etvBottomSheet.editText.filters = arrayOf(InputFilter.LengthFilter(16))
      etvBottomSheet.state = if (isError) LinkMindEditTextState.ERROR else LinkMindEditTextState.ENABLE
      btnBottomSheet.state = if (!isError && isTextLongEnough()) LinkMIndFullWidthButtonState.ENABLE_PRIMARY else LinkMIndFullWidthButtonState.DISABLE
    }
  }

  private fun isTextLongEnough() = binding.etvBottomSheet.editText.text.length > 1
  fun setTitle(@StringRes textId: Int) {
    binding.tvBottomSheetTitle.setText(textId)
  }

  fun showErrorMsg(bottomSheetType: BottomSheetType): Boolean {
    return when (bottomSheetType) {
      BottomSheetType.LINK -> {
        binding.etvBottomSheet.editText.text.isEmpty()
      }

      BottomSheetType.CLIP -> {
        binding.etvBottomSheet.editText.text.length > 15 || binding.etvBottomSheet.editText.text.isEmpty()
      }
    }
  }

  fun setErroMsg(@StringRes textId: Int) {
    binding.tvBottomSheetErrorText.setText(textId)
  }

  fun showSnackBar(message: String, isLongDuration: Boolean) {
    context.linkMindSnackBar(binding.root, message, isLongDuration)
  }

  fun getText(): String = binding.etvBottomSheet.editText.text.toString()

  fun show() {
    bottomSheetDialog.show()
  }

  fun dismiss() {
    bottomSheetDialog.dismiss()
  }
}
