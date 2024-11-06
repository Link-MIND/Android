package org.sopt.clip.clipedit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.sopt.clip.databinding.ItemClipEditClipBinding
import org.sopt.model.category.Category
import org.sopt.ui.view.ItemDiffCallback

class ClipEditAdapter(
  private val itemClick: (Long, String, Long, String) -> Unit,
) : ListAdapter<Category, ClipEditViewHolder>(DiffUtil) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipEditViewHolder {
    return ClipEditViewHolder(
      ItemClipEditClipBinding.inflate(LayoutInflater.from(parent.context), parent, false),
      itemClick,
    )
  }

  override fun onBindViewHolder(holder: ClipEditViewHolder, position: Int) {
    holder.onBind(getItem(position))
  }

  fun moveItem(fromPosition: Int, toPosition: Int) {
    val currentList = currentList.toMutableList()
    val item = currentList.removeAt(fromPosition)
    currentList.add(toPosition, item)
    submitList(currentList)
  }

  fun getCategoryId(position: Int): Long? {
    return currentList[position].categoryId
  }

  companion object {
    private val DiffUtil = ItemDiffCallback<Category>(
      onItemsTheSame = { old, new -> old.categoryId == new.categoryId },
      onContentsTheSame = { old, new -> old == new },
    )
  }
}
