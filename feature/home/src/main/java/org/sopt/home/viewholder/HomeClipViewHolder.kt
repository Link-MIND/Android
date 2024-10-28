package org.sopt.home.viewholder

import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import org.sopt.home.databinding.ItemHomeClipBinding
import org.sopt.model.category.Category

class HomeClipViewHolder(
  private val binding: ItemHomeClipBinding,
  private val onClickClip: (Category) -> Unit,
  private val onClickEmptyClip: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
  fun onBind(data: Category?, position: Int) {
    binding.tvLinkTitle.text = "sa"
    binding.tvLinkUrl.text = "asd"
    binding.tvLinkClipTitle.text = "asdsd"
  }
}
