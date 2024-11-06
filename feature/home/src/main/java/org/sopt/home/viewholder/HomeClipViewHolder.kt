package org.sopt.home.viewholder

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.sopt.home.databinding.ItemHomeClipBinding
import org.sopt.model.home.RecentSavedLink
import org.sopt.ui.view.onThrottleClick

class HomeClipViewHolder(
  private val binding: ItemHomeClipBinding,
  private val onClickClip: (RecentSavedLink) -> Unit,
  private val onClickEmptyClip: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
  fun onBind(data: RecentSavedLink?, position: Int) {
    if (data == null) {
      with(binding) {
        clHomeItemClip.isGone = true
        clHomeItemClipEmpty.isVisible = true
        root.setOnClickListener {
          onClickEmptyClip()
        }
      }
      return
    }
    with(binding) {
      tvLinkTitle.text = data.toastTitle
      tvLinkUrl.text = data.linkUrl
      binding.tvLinkTitle.setVisible(!data.categoryTitle.isNullOrEmpty())
      tvLinkClipTitle.text = data.categoryTitle
      ivLinkThumnail.load(data.thumbnailUrl)
      tvItemClipLink.setVisible(data.isRead)
      root.onThrottleClick {
        onClickClip.invoke(data)
      }
    }
  }

  private fun View.setVisible(value: Boolean) {
    visibility = if (value) View.VISIBLE else View.GONE
  }
}
