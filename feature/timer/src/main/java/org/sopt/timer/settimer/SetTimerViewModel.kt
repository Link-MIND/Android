package org.sopt.timer.settimer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.sopt.model.timer.TimerData
import org.sopt.timer.dummymodel.Clip
import org.sopt.model.timer.Repeat
import org.sopt.timer.dummymodel.TimePicker
import org.sopt.timer.usecase.PostTimerUseCase
import org.sopt.ui.view.UiState
import javax.inject.Inject

@HiltViewModel
class SetTimerViewModel @Inject constructor(
  private val postTimerUseCase: PostTimerUseCase
) : ViewModel() {
  private val _clipList = MutableStateFlow<List<Clip>>(emptyList())
  val clipList: StateFlow<List<Clip>> = _clipList.asStateFlow()

  private val _repeatList = MutableStateFlow<List<Repeat>>(emptyList())
  val repeatList: StateFlow<List<Repeat>> = _repeatList.asStateFlow()

  private val _selectedTime = MutableStateFlow(TimePicker("오전", "01", "00"))
  val selectedTime: StateFlow<TimePicker> = _selectedTime.asStateFlow()

  private val _postTimerState = MutableStateFlow<UiState<Any>>(UiState.Empty)
  val postTimerState: StateFlow<UiState<Any>> = _postTimerState.asStateFlow()

  val currentHourIndex = MutableStateFlow(1)
  val currentMinuteIndex = MutableStateFlow(1)
  val currentAmPmIndex = MutableStateFlow(1)

  fun initSetTimer() {
    _clipList.value = listOf(
      Clip("전체 클립", 3, false),
      Clip("전체 클립", 3, false),
      Clip("전체 클립", 3, false),
      Clip("전체 클립", 3, false),
    )
    _repeatList.value = listOf(
      Repeat("매일 (월~일)", false),
      Repeat("주중마다 (월~금)", false),
      Repeat("주말마다 (토~일)", false),
      Repeat("월요일마다", false),
      Repeat("화요일마다", false),
      Repeat("수요일마다", false),
      Repeat("목요일마다", false),
      Repeat("금요일마다", false),
      Repeat("토요일마다", false),
      Repeat("일요일마다", false),
    )
    _selectedTime.value = TimePicker("오전", "01", "00")
    _postTimerState.value = UiState.Empty

    currentHourIndex.value = 1

    currentMinuteIndex.value = 1

    currentAmPmIndex.value = 1
  }

  fun postTimer(){
    viewModelScope.launch {
      _postTimerState.emit(UiState.Loading)
      val category = clipList.value.first { it.isSelected }
      val time = "${selectedTime.value.hour}:${selectedTime.value.minute}"
      postTimerUseCase(/*category.*/17,time,repeatList.value).onSuccess {
        Log.e("성공", "성공")
        _postTimerState.emit(UiState.Success(it))
      }.onFailure {
        Log.e("실패", "${it.message}")
        _postTimerState.emit(UiState.Failure(it.message.toString()))
      }
    }
  }

  fun setSelectedHour(hour: String) {
    val timePicker = TimePicker(selectedTime.value.timePeriod, hour, selectedTime.value.minute)
    _selectedTime.value = timePicker
  }

  fun setSelectedMinute(minute: String) {
    val timePicker = TimePicker(selectedTime.value.timePeriod, selectedTime.value.hour, minute)
    _selectedTime.value = timePicker
  }

  fun setSelectedPeriod(period: String) {
    val timePicker = TimePicker(period, selectedTime.value.hour, selectedTime.value.minute)
    _selectedTime.value = timePicker
  }

  fun setClipList(clipList: List<Clip>) {
    _clipList.value = clipList
  }

  fun setRepeatList(repeatList: List<Repeat>) {
    _repeatList.value = repeatList
  }
}
