package org.sopt.clip.clipchange

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import org.sopt.clip.databinding.ItemClipChangeBinding
import org.sopt.model.timer.Clip
import org.sopt.ui.view.ItemDiffCallback

class ClipChangeAdapter(
  private val onClick: (Clip, Int) -> Unit,
  private val context: Context,
) : ListAdapter<Clip, ClipChangeViewHolder>(DiffUtil) {
  var selectedPosition = -1
  override fun onBindViewHolder(holder: ClipChangeViewHolder, position: Int) {
    holder.onBind(getItem(position), position) { clip, position ->
      selectItemByPosition(position, clip)
      onClick(clip, position)
    }

    if (position == 0) {
      val disMissClickColor = ContextCompat.getColor(context, org.sopt.mainfeature.R.color.neutrals400)
      holder.binding.ivItemClipChange.setColorFilter(disMissClickColor)
      holder.binding.tvItemClipChangeName.setTextColor(disMissClickColor)
      holder.binding.tvItemClipChangeCount.setTextColor(disMissClickColor)
      holder.binding.root.isEnabled = false
    }
  }

  private fun selectItemByPosition(position: Int, clip: Clip) {
    if (selectedPosition != position) {
      if (selectedPosition != -1) {
        getItem(selectedPosition).isSelected = false
        notifyItemChanged(selectedPosition)
      }
      clip.isSelected = true
      selectedPosition = position
    } else {
      clip.isSelected = !clip.isSelected
      if (!clip.isSelected) {
        selectedPosition = -1
      }
    }
    notifyItemChanged(position)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipChangeViewHolder {
    return ClipChangeViewHolder(
      ItemClipChangeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
      context,
    )
  }

  companion object {
    private val DiffUtil = ItemDiffCallback<Clip>(
      onItemsTheSame = { old, new -> old == new },
      onContentsTheSame = { old, new -> old == new },
    )
  }
}
