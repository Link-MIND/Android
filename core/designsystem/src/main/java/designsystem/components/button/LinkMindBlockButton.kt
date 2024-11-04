package designsystem.components.button

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import designsystem.components.button.state.LinkMindButtonState
import org.sopt.mainfeature.R
import org.sopt.mainfeature.databinding.ButtonBlockLinkmindBinding
import org.sopt.ui.view.onThrottleClick

@SuppressLint("CustomViewStyleable")
class LinkMindBlockButton @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

  private val binding: ButtonBlockLinkmindBinding

  var state: LinkMindButtonState = LinkMindButtonState.ENABLE
    set(value) {
      field = value
      when (field) {
        LinkMindButtonState.ENABLE -> {
          setBtnEnable(R.drawable.shape_neutrals850_fill_12_rect)
        }

        LinkMindButtonState.DISABLE -> {
          setBtnDisable(R.drawable.shape_neutrals200_fill_12_rect)
        }
      }
    }

  private fun setBtnEnable(drawableResId: Int) {
    binding.apply {
      clBtnMediumWidthLinkmind.isClickable = true
      clBtnMediumWidthLinkmind.isFocusable = true
      tvBtn.setTextColor(ContextCompat.getColor(context, R.color.white))
      clBtnMediumWidthLinkmind.setBackgroundResource(drawableResId)
    }
  }

  private fun setBtnDisable(drawableResId: Int) {
    binding.apply {
      clBtnMediumWidthLinkmind.isClickable = false
      clBtnMediumWidthLinkmind.isFocusable = false
      tvBtn.setTextColor(ContextCompat.getColor(context, R.color.white))
      clBtnMediumWidthLinkmind.setBackgroundResource(drawableResId)
    }
  }

  fun btnClick(onClick: () -> Unit) {
    binding.clBtnMediumWidthLinkmind.onThrottleClick {
      onClick()
    }
  }

  fun showLoadingUsingSaveLink(loadingText: String = "저장 중...") {
    binding.apply {
      progressBar.visibility = View.VISIBLE
      tvBtn.text = loadingText
      state = LinkMindButtonState.DISABLE
    }
  }

  init {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    binding = ButtonBlockLinkmindBinding.inflate(inflater, this, true)

    val typedArray =
      context.obtainStyledAttributes(attrs, R.styleable.LinkMindButtonFullWidth, defStyleAttr, 0)

    val background = typedArray.getResourceId(
      R.styleable.LinkMindButtonFullWidth_fullWidthBtnBackGroundTint,
      R.drawable.ripple_btn,
    )
    val text = typedArray.getText(R.styleable.LinkMindButtonFullWidth_fullWidthBtnText)
    val textColor = typedArray.getColor(
      R.styleable.LinkMindButtonFullWidth_fullWidthBtnTextColor,
      ContextCompat.getColor(context, R.color.black),
    )

    binding.apply {
      clBtnMediumWidthLinkmind.setBackgroundResource(background)
      tvBtn.text = text
      tvBtn.setTextColor(textColor)
    }

    typedArray.recycle()
  }
}
