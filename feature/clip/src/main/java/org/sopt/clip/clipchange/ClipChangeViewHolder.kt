package org.sopt.clip.clipchange

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import org.sopt.clip.databinding.ItemClipChangeBinding
import org.sopt.model.timer.Clip
import org.sopt.ui.view.onThrottleClick

class ClipChangeViewHolder(
  val binding: ItemClipChangeBinding,
  val context: Context,
) : RecyclerView.ViewHolder(binding.root) {
  fun onBind(data: Clip?, pos: Int, onClick: (Clip, Int) -> Unit) {
    if (data == null) return
    with(binding) {
      tvItemClipChangeName.text = data.name
      tvItemClipChangeCount.text = "${data.count}개"
      setSelectedClipColor(data, pos)
      root.onThrottleClick {
        onClick(data, bindingAdapterPosition)
        bindingAdapter?.notifyItemChanged(pos)
      }
    }
  }

  private fun ItemClipChangeBinding.setSelectedClipColor(
    data: Clip,
    pos: Int,
  ) {
    val selectedColor = androidx.core.content.ContextCompat.getColor(context, org.sopt.mainfeature.R.color.primary)
    val defaultColor = androidx.core.content.ContextCompat.getColor(context, org.sopt.mainfeature.R.color.neutrals900)
    if (data.isSelected) {
      ivItemClipChange.setImageResource(
        org.sopt.mainfeature.R.drawable.ic_clip_all_24_primary.takeIf { pos == 0 }
          ?: org.sopt.mainfeature.R.drawable.ic_clip_24_primary,
      )
      tvItemClipChangeCount.setTextColor(selectedColor)
      tvItemClipChangeName.setTextColor(selectedColor)
    } else {
      ivItemClipChange.setImageResource(
        org.sopt.mainfeature.R.drawable.ic_clip_all_24.takeIf { pos == 0 }
          ?: org.sopt.mainfeature.R.drawable.ic_clip_24,
      )
      tvItemClipChangeCount.setTextColor(defaultColor)
      tvItemClipChangeName.setTextColor(defaultColor)
    }
  }
}
