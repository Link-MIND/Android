package org.sopt.timer

import androidx.recyclerview.widget.RecyclerView
import org.sopt.model.timer.Timer
import org.sopt.timer.databinding.ItemTimerWaitBinding
import org.sopt.ui.view.onThrottleClick

class WaitTimerViewHolder(
  private val binding: ItemTimerWaitBinding,
  private val onToggleClicked: (Timer) -> Unit,
  private val onMoreClicked: (Timer) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

  fun onBind(data: Timer?) {
    if (data == null) return
    with(binding) {
      tvItemTimerWaitCategory.text = data.comment
      tvItemTimerWaitWhen.text = TIME_FORMAT.format(data.remindDates, data.remindTime)
      tgItemTimerWait.initToggleState(data.isAlarm!!)
      tgItemTimerWait.onThrottleClick {
        onToggleClicked(data)
      }
      ivItemTimerWaitMore.onThrottleClick {
        onMoreClicked(data)
      }
    }
  }

  companion object {
    private const val TIME_FORMAT = "매주 %s %s마다"
  }
}
