package org.sopt.clip.clipedit

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.sopt.clip.databinding.ItemClipEditClipBinding
import org.sopt.model.category.Category

class ClipEditViewHolder(
  private val binding: ItemClipEditClipBinding,
  private val onClickItemClip: (Long, String, Long, String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
  fun onBind(clipData: Category) {
    if (clipData == null) return
    with(binding) {
      tvClipEditTitle.text = clipData.categoryTitle
      if (clipData.categoryId!!.toInt() == 0) {
        ivClipEditDelete.visibility = View.GONE
        ivClipEditTitleEdit.visibility = View.GONE
        ivClipEditFix.visibility = View.VISIBLE
      } else {
        ivClipEditDelete.visibility = View.VISIBLE
        ivClipEditTitleEdit.visibility = View.VISIBLE
        ivClipEditFix.visibility = View.GONE
        ivClipEditTitleEdit.setOnClickListener {
          onClickItemClip(clipData.categoryId!!.toLong(), "edit", itemId, clipData.categoryTitle ?: "")
        }
        ivClipEditDelete.setOnClickListener {
          onClickItemClip(clipData.categoryId!!.toLong(), "delete", itemId, clipData.categoryTitle ?: "")
        }
      }
    }
  }
}
