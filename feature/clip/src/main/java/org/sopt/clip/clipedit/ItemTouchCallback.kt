package org.sopt.clip.clipedit

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchCallback(
  private val adapter: ClipEditAdapter,
  private val recyclerView: RecyclerView,
  private val onMoveItem: (Long?, Int) -> Unit,
) : ItemTouchHelper.Callback() {

  override fun getMovementFlags(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
  ): Int {
    return if (viewHolder.bindingAdapterPosition == 0) {
      0
    } else {
      val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
      return makeMovementFlags(dragFlags, 0)
    }
  }

  override fun onMove(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    target: RecyclerView.ViewHolder,
  ): Boolean {
    val fromPosition = viewHolder.bindingAdapterPosition
    val toPosition = target.bindingAdapterPosition

    if (fromPosition == 0 || toPosition == 0) {
      return false
    }
    adapter.moveItem(fromPosition, toPosition)
    return true
  }

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
  }

  override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
    super.clearView(recyclerView, viewHolder)
    onMoveItem(adapter.getCategoryId(viewHolder.bindingAdapterPosition), viewHolder.bindingAdapterPosition)
    viewHolder.itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
  }

  override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
    super.onSelectedChanged(viewHolder, actionState)
    viewHolder?.itemView?.animate()?.scaleX(1.1f)?.scaleY(1.1f)?.setDuration(200)?.start()
  }
}
