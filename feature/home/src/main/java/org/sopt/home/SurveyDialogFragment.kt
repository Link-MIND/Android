package org.sopt.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import org.sopt.home.databinding.FragmentSurveyDialogBinding
import org.sopt.ui.base.BindingDialogFragment
import org.sopt.ui.view.onThrottleClick

@AndroidEntryPoint
class SurveyDialogFragment : BindingDialogFragment<FragmentSurveyDialogBinding>(
  { FragmentSurveyDialogBinding.inflate(it) },
) {
  private var imageUrl: String? = null
  private var linkUrl: () -> Unit = {}
  private var handleSkip: () -> Unit = {}
  private var popupVisibility: () -> Unit = {}

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    imageUrl = arguments?.getString("imageUrl")
    setStyle(STYLE_NO_FRAME, android.R.style.Theme_Dialog)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    popupVisibility.invoke()
    setSurveyImage(imageUrl)

    binding.ivSurveyDialogClose.onThrottleClick {
      dismiss()
    }

    binding.btnSurveyDialog.onThrottleClick {
      linkUrl.invoke()
      dismiss()
    }

    binding.btnSurveyDialogSkip.onThrottleClick {
      handleSkip.invoke()
      dismiss()
    }
  }

  override fun onStart() {
    super.onStart()
    val dialog = dialog
    if (dialog != null) {
      dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
  }

  private fun setSurveyImage(text: String?) =
    binding.ivSurveyDialog.load(text)

  companion object {
    fun newInstance(
      imageUrl: String,
      onNavigateWebView: () -> Unit,
      onNegativeButtonClick: () -> Unit,
      setPopupVisibility: () -> Unit,
    ): SurveyDialogFragment {
      val args = Bundle().apply {
        putString("imageUrl", imageUrl)
      }
      return SurveyDialogFragment().apply {
        arguments = args
        linkUrl = onNavigateWebView
        handleSkip = onNegativeButtonClick
        popupVisibility = setPopupVisibility
      }
    }
  }
}
