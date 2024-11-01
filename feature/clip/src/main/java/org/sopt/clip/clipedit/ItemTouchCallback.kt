package org.sopt.clip.clipedit

import android.util.Log
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.sopt.clip.R
import org.sopt.model.category.Category

class ItemTouchCallback(
  private val adapter: ClipEditAdapter,
  private val recyclerView: RecyclerView,
  private val onClearView: (List<Category>) -> Unit,
) : ItemTouchHelper.Callback() {

  override fun getMovementFlags(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
  ): Int {
    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
    return makeMovementFlags(dragFlags, 0)
  }

  override fun onMove(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    target: RecyclerView.ViewHolder,
  ): Boolean {
    val fromPosition = viewHolder.bindingAdapterPosition
    val toPosition = target.bindingAdapterPosition
    adapter.moveItem(fromPosition, toPosition)
    return true
  }

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
  }

  override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
    super.clearView(recyclerView, viewHolder)

    val finalList = adapter.getList()
    val fifinalList: MutableList<Category> = mutableListOf()
    finalList.withIndex().forEach {
      fifinalList.add(Category(categoryId = it.value.categoryId, categoryTitle = it.value.categoryTitle, toastNum = it.value.toastNum))
    }

    Log.e("리스트", fifinalList.toString())

    onClearView(fifinalList)

    viewHolder.itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
  }

  override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
    super.onSelectedChanged(viewHolder, actionState)
    viewHolder?.itemView?.let {
      it.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
    }
  }
}
