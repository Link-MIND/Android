package org.sopt.timer.settimer.clipselect

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.sopt.model.timer.Clip
import org.sopt.timer.databinding.ItemTimerClipSelectBinding
import org.sopt.ui.view.onThrottleClick

class ClipSelectViewHolder(
  val binding: ItemTimerClipSelectBinding,
  val context: Context,
) : RecyclerView.ViewHolder(binding.root) {
  fun onBind(data: Clip?, pos: Int, onClick: (Clip, Int) -> Unit) {
    if (data == null) return
    with(binding) {
      tvItemTimerClipName.text = data.name
      tvItemTimerClipCount.text = "${data.count}개"
      setSelectedClipColor(data, pos)
      root.onThrottleClick {
        onClick(data, bindingAdapterPosition)
        bindingAdapter?.notifyItemChanged(pos)
      }
    }
  }

  private fun ItemTimerClipSelectBinding.setSelectedClipColor(
    data: Clip,
    pos: Int,
  ) {
    val selectedColor = ContextCompat.getColor(context, org.sopt.mainfeature.R.color.primary)
    val defaultColor = ContextCompat.getColor(context, org.sopt.mainfeature.R.color.neutrals900)
    if (data.isSelected) {
      ivItemTimerClip.setImageResource(
        org.sopt.mainfeature.R.drawable.ic_clip_all_24_primary.takeIf { pos == 0 }
          ?: org.sopt.mainfeature.R.drawable.ic_clip_24_primary,
      )
      tvItemTimerClipCount.setTextColor(selectedColor)
      tvItemTimerClipName.setTextColor(selectedColor)
    } else {
      ivItemTimerClip.setImageResource(
        org.sopt.mainfeature.R.drawable.ic_clip_all_24.takeIf { pos == 0 }
          ?: org.sopt.mainfeature.R.drawable.ic_clip_24,
      )
      tvItemTimerClipCount.setTextColor(defaultColor)
      tvItemTimerClipName.setTextColor(defaultColor)
    }
  }
}
