package org.sopt.clip.webview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.sopt.datastore.datastore.SecurityDataStore
import org.sopt.domain.link.usecase.PatchReadLinkUseCase
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor(
  private val patchReadLinkUseCase: PatchReadLinkUseCase,
  private val dataStore: SecurityDataStore,
) : ViewModel() {
  val patchReadLinkResult = MutableStateFlow(false)
  val tooltip = MutableStateFlow(false)
  val tooltip2 = MutableStateFlow(false)
  fun patchReadLink(toastId: Long, isRead: Boolean) = viewModelScope.launch {
    patchReadLinkUseCase(param = PatchReadLinkUseCase.Param(toastId, isRead)).onSuccess {
      patchReadLinkResult.emit(it)
    }.onFailure {
      Log.e("실패", it.message.toString())
    }
  }
  fun showThenHide(showDelay: Long = 500, duration: Long = 2000) = viewModelScope.launch(Dispatchers.IO) {
    runCatching {
      val booleanListFlow =
        dataStore.flowTooltip().first().toString()
      val stringValue = booleanListFlow.split(",").map { it.toBoolean() }
      if (stringValue[2]) {
        delay(showDelay)
        tooltip.emit(true)
        delay(duration)
        tooltip.emit(false)
        tooltip2.emit(true)
        delay(duration)
        tooltip2.emit(false)
        dataStore.setTooltip(listOf(stringValue[0], stringValue[1], !stringValue[2], stringValue[3]).joinToString(","))
      }
    }
  }
}
