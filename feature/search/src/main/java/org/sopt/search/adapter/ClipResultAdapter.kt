package org.sopt.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.sopt.model.category.Category
import org.sopt.search.databinding.ItemSearchResultClipBinding
import org.sopt.search.viewholder.ClipResultViewHolder
import org.sopt.ui.view.ItemDiffCallback

class ClipResultAdapter(
  private val onClick: (Category) -> Unit,
) :
  ListAdapter<Category, ClipResultViewHolder>(DiffUtil) {

  private var searchQuery: String = ""

  fun setSearchQuery(query: String) {
    searchQuery = query
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipResultViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = ItemSearchResultClipBinding.inflate(inflater, parent, false)
    return ClipResultViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ClipResultViewHolder, position: Int) {
    val result = getItem(position)
    holder.onBind(result, onClick)
  }

  companion object {
    private val DiffUtil = ItemDiffCallback<Category>(
      onItemsTheSame = { old, new -> old.categoryId == new.categoryId },
      onContentsTheSame = { old, new -> old == new },
    )
  }
}
