package org.sopt.mypage.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.sopt.auth.repository.AuthRepository
import org.sopt.model.user.MyPageData
import org.sopt.model.user.SettingPageData
import org.sopt.ui.view.UiState
import org.sopt.user.usecase.GetUserMyPageUseCase
import org.sopt.user.usecase.GetUserSettingUseCase
import org.sopt.user.usecase.PatchPushUseCase
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val getUserSettingUseCase: GetUserSettingUseCase,
  private val patchPushUseCase: PatchPushUseCase,
  private val getUserMyPageUseCase: GetUserMyPageUseCase,
  private val authRepository: AuthRepository,
) : ViewModel() {
  private val _logoutState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
  val logoutState: StateFlow<UiState<Unit>> = _logoutState.asStateFlow()

  private val _withdrawState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
  val withdrawState: StateFlow<UiState<Unit>> = _withdrawState.asStateFlow()

  private val _settingState = MutableStateFlow<UiState<SettingPageData>>(UiState.Empty)
  val settingState: StateFlow<UiState<SettingPageData>> = _settingState.asStateFlow()

  private val _myPageState = MutableStateFlow<UiState<MyPageData>>(UiState.Empty)
  val myPageState: StateFlow<UiState<MyPageData>> = _myPageState.asStateFlow()
  fun getUserMyPage() = viewModelScope.launch {
    getUserMyPageUseCase.invoke().onSuccess { data ->
      _myPageState.emit(UiState.Success(data))
    }.onFailure { error ->
      _myPageState.emit(UiState.Failure(error.toString()))
    }
  }

  val pushIsAllowed = MutableStateFlow(true)

  fun getUserInfo() = viewModelScope.launch {
    getUserSettingUseCase.invoke().onSuccess { data ->
      _settingState.emit(UiState.Success(data))
      pushIsAllowed.emit(data.fcmIsAllowed)
    }.onFailure { error ->
      Log.d("UserSetting", "$error")
    }
  }

  fun patchPush(allowedPush: Boolean) = viewModelScope.launch {
    patchPushUseCase.invoke(allowedPush).onSuccess { data ->
      pushIsAllowed.emit(data)
    }.onFailure {
      Log.e("실패", it.message.toString())
    }
  }

  fun logout() = viewModelScope.launch {
    authRepository.signout().onSuccess {
      _logoutState.emit(UiState.Success(it))
    }.onFailure {
      _logoutState.emit(UiState.Failure(it.message.toString()))
    }
  }

  fun withdraw() = viewModelScope.launch {
    authRepository.withdraw().onSuccess {
      _withdrawState.emit(UiState.Success(it))
    }.onFailure {
      _withdrawState.emit(UiState.Failure(it.message.toString()))
    }
  }
}
