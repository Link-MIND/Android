package org.sopt.home.viewholder

import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.sopt.home.databinding.ItemHomeClipBinding
import org.sopt.model.category.Category
import org.sopt.model.home.RecentSavedLink

class HomeClipViewHolder(
  private val binding: ItemHomeClipBinding,
  private val onClickClip: (Category) -> Unit,
  private val onClickEmptyClip: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
  fun onBind(data: RecentSavedLink?, position: Int) {
    if (data==null) {
      with(binding) {
        clHomeItemClip.isGone = true
        clHomeItemClipEmpty.isVisible = true
        root.setOnClickListener {
          onClickEmptyClip()
          //TODO.  onClick 수정, ui삐꾸난거 잡기
        }
      }
      return
    }
    with(binding) {
      tvLinkTitle.text = data.toastTitle
      tvLinkUrl.text = data.linkUrl
      if (data.categoryTitle.isNullOrEmpty()) {
        tvLinkClipTitle.isGone = true
      } else {
        tvLinkClipTitle.isVisible = true
        tvLinkClipTitle.text = data.categoryTitle
      }
      ivLinkThumnail.load(data.thumbnailUrl)
      if (data.isRead) {
        tvItemClipLink.isVisible = true
      } else {
        tvItemClipLink.isGone = true
      }
    }
  }
}
