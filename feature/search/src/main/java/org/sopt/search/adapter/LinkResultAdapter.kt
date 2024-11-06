package org.sopt.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.sopt.model.category.Toast
import org.sopt.search.databinding.ItemSearchLinkBinding
import org.sopt.search.viewholder.LinkResultViewHolder
import org.sopt.ui.view.ItemDiffCallback

class LinkResultAdapter(
  private val onClick: (Toast) -> Unit,
) :
  ListAdapter<Toast, LinkResultViewHolder>(DiffUtil) {

  private var searchQuery: String = ""

  fun setSearchQuery(query: String) {
    searchQuery = query
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkResultViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = ItemSearchLinkBinding.inflate(inflater, parent, false)
    return LinkResultViewHolder(binding)
  }

  override fun onBindViewHolder(holder: LinkResultViewHolder, position: Int) {
    val result = getItem(position)
    holder.onBind(result, searchQuery, onClick)
  }

  companion object {
    private val DiffUtil = ItemDiffCallback<Toast>(
      onItemsTheSame = { old, new -> old.toastId == new.toastId },
      onContentsTheSame = { old, new -> old == new },
    )
  }
}
