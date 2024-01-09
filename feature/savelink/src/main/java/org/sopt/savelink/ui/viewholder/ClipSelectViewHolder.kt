package org.sopt.savelink.ui.viewholder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.sopt.savelink.databinding.ItemTimerClipSelectBinding
import org.sopt.savelink.ui.Clip

class ClipSelectViewHolder(
  val binding: ItemTimerClipSelectBinding,
  val context: Context,
) : RecyclerView.ViewHolder(binding.root) {
  fun onBind(data: Clip?, positon: Int, onClick: (Clip, Int) -> Unit) {
    if (data == null) return
    if (positon == 0) {
      binding.ivItemTimerClip.setImageResource(org.sopt.mainfeature.R.drawable.ic_clip_all_24)
    } else {
      binding.ivItemTimerClip.setImageResource(org.sopt.mainfeature.R.drawable.ic_clip_24)
    }
    with(binding) {
      tvItemTimerClipName.text = data.name
      tvItemTimerClipCount.text = "${data.count}개"
      val selectedColor = ContextCompat.getColor(context, org.sopt.mainfeature.R.color.primary)
      val defaultColor = ContextCompat.getColor(context, org.sopt.mainfeature.R.color.neutrals900)
      if (data.isSelected) {
        tvItemTimerClipCount.setTextColor(selectedColor)
        tvItemTimerClipName.setTextColor(selectedColor)
        if (positon == 0) {
          ivItemTimerClip.setImageResource(org.sopt.mainfeature.R.drawable.ic_clip_all_24_primary)
        } else {
          ivItemTimerClip.setImageResource(org.sopt.mainfeature.R.drawable.ic_clip_24_primary)
        }
      } else {
        if (positon == 0) {
          ivItemTimerClip.setImageResource(org.sopt.mainfeature.R.drawable.ic_home_clip_20)
        } else {
          ivItemTimerClip.setImageResource(org.sopt.mainfeature.R.drawable.ic_clip_24)
        }
        tvItemTimerClipCount.setTextColor(defaultColor)
        tvItemTimerClipName.setTextColor(defaultColor)
      }
      root.setOnClickListener {
        onClick(data, bindingAdapterPosition)
      }
    }
  }
}
