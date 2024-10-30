package org.sopt.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.sopt.home.databinding.ItemHomeClipBinding
import org.sopt.home.viewholder.HomeClipViewHolder
import org.sopt.model.category.Category
import org.sopt.model.home.RecentSavedLink
import org.sopt.ui.view.ItemDiffCallback

class HomeClipAdapter(
  private val onClickClip: (Category) -> Unit,
  private val onClickEmptyClip: () -> Unit,
) : ListAdapter<RecentSavedLink, HomeClipViewHolder>(DiffUtil) {
  override fun onBindViewHolder(holder: HomeClipViewHolder, position: Int) {
    holder.onBind(getItem(position), position)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeClipViewHolder {
    return HomeClipViewHolder(
      ItemHomeClipBinding.inflate(LayoutInflater.from(parent.context), parent, false),
      onClickClip,
      onClickEmptyClip,
    )
  }

  override fun getItemCount() = currentList.size.coerceAtMost(3)

  companion object {
    private val DiffUtil = ItemDiffCallback<RecentSavedLink>(
      onItemsTheSame = { old, new -> old.toastId == new.toastId },
      onContentsTheSame = { old, new -> old == new },
    )
  }
}
