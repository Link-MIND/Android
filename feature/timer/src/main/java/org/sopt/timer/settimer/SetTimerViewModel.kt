package org.sopt.timer.settimer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.sopt.domain.category.category.usecase.GetCategoryAllUseCase
import org.sopt.model.category.Category
import org.sopt.model.timer.Repeat
import org.sopt.model.timer.Clip
import org.sopt.timer.model.TimePicker
import org.sopt.model.timer.toUiModel
import org.sopt.timer.usecase.FormatRepeatListToIntList
import org.sopt.timer.usecase.FormatRepeatListToStringList
import org.sopt.timer.usecase.PatchTimerUseCase
import org.sopt.timer.usecase.PostTimerUseCase
import org.sopt.ui.view.UiState
import javax.inject.Inject

@HiltViewModel
class SetTimerViewModel @Inject constructor(
  private val postTimerUseCase: PostTimerUseCase,
  private val formatRepeatListToStringList: FormatRepeatListToStringList,
  private val patchTimerUseCase: PatchTimerUseCase,
  private val formatRepeatListToIntList: FormatRepeatListToIntList,
  private val getCategoryAllUseCase: GetCategoryAllUseCase,
) : ViewModel() {
  private val _clipState = MutableStateFlow<UiState<List<Clip>>>(UiState.Empty)
  val clipState: StateFlow<UiState<List<Clip>>> = _clipState.asStateFlow()

  private val _repeatList = MutableStateFlow<List<Repeat>>(emptyList())
  val repeatList: StateFlow<List<Repeat>> = _repeatList.asStateFlow()

  val selectedList: StateFlow<List<String>> get() {
    return MutableStateFlow(formatRepeatListToStringList(_repeatList.value))
  }

  private val _selectedTime = MutableStateFlow(TimePicker("오전", "01", "00"))
  val selectedTime: StateFlow<TimePicker> = _selectedTime.asStateFlow()

  private val _postTimerState = MutableStateFlow<UiState<Any>>(UiState.Empty)
  val postTimerState: StateFlow<UiState<Any>> = _postTimerState.asStateFlow()

  val currentHourIndex = MutableStateFlow(1)
  val currentMinuteIndex = MutableStateFlow(1)
  val currentAmPmIndex = MutableStateFlow(1)

  val isFirst = MutableStateFlow(true)
  fun initSetTimer() {
    _clipState.value = UiState.Empty
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

    isFirst.value = true
  }

  fun getCategoryAll() {
    viewModelScope.launch {
      getCategoryAllUseCase().onSuccess {
        val list: MutableList<Category> = it.categories.toMutableList()
        list.add(0, Category(0, "전체 클립", it.toastNumberInEntire))
        _clipState.emit(UiState.Success(list.toUiModel()))
      }.onFailure {
        Log.e("실패", it.message.toString())
      }
    }
  }

  fun postTimer() {
    viewModelScope.launch {
      _postTimerState.emit(UiState.Loading)
      val categoryId = (clipState.value as UiState.Success).data.first { it.isSelected }.id
      var hour = _selectedTime.value.hour.toInt()
      if (_selectedTime.value.timePeriod == "오후") hour += 12
      if (hour == 12 || hour == 24) {
        hour -= 12
      }
      val time = "${ if (hour < 10) "0$hour" else hour.toString()}:${selectedTime.value.minute}"
      postTimerUseCase(categoryId, time, formatRepeatListToIntList(repeatList.value)).onSuccess {
        Log.e("성공", "성공")
        _postTimerState.emit(UiState.Success(it))
      }.onFailure {
        Log.e("실패", "${it.message}")
        _postTimerState.emit(UiState.Failure(it.message.toString()))
      }
    }
  }

  fun patchTimer(timerId: Int) {
    viewModelScope.launch {
      _postTimerState.emit(UiState.Loading)
      var hour = _selectedTime.value.hour.toInt()
      if (_selectedTime.value.timePeriod == "오후") hour += 12
      if (hour == 12) hour = 0
      if (hour == 24) hour = 12
      val time = "${ if (hour < 10) "0$hour" else if (hour == 24) "00" else hour.toString()}:${selectedTime.value.minute}"
      patchTimerUseCase(timerId, time, formatRepeatListToIntList(repeatList.value)).onSuccess {
        Log.e("성공", "성공")
        _postTimerState.emit(UiState.Success(it))
      }.onFailure {
        Log.e("실패", it.message.toString())
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
    _clipState.value = UiState.Success(clipList)
  }

  fun setRepeatList(repeatList: List<Repeat>) {
    _repeatList.value = repeatList
  }

  fun setRepeat(num: Int) {
    _repeatList.value[num].isSelected = !(_repeatList.value[num].isSelected)
  }
}
