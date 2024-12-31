package org.sopt.maincontainer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import org.sopt.datastore.datastore.SecurityDataStore
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val dataStore: SecurityDataStore,
) : ContainerHost<MainState, MainSideEffect>, ViewModel() {
  override val container: Container<MainState, MainSideEffect> =
    container(MainState())

  fun showThenHide(showDelay: Long = 3100, duration: Long = 2000) = intent {
    runCatching {
      val booleanListFlow =
        dataStore.flowTooltip().first().toString()
      val stringValue = booleanListFlow.split(",").map { it.toBoolean() }
      if (!stringValue[0]) {
        delay(showDelay)
        reduce {
          state.copy(visibleBubbleMark = true)
        }
        delay(duration)
        reduce {
          state.copy(visibleBubbleMark = false)
        }
      }
    }
  }

  fun updateClipBoard(clipboard: String) = intent {
    reduce {
      state.copy(clipboard = clipboard)
    }
    dataStore.setRecentLink(clipboard)
  }

  fun getRecentLink() = intent {
    if (dataStore.flowRecentLink().first().isNullOrEmpty()) return@intent
    val clipboard = dataStore.flowRecentLink().first()
    reduce {
      state.copy(clipboard = clipboard)
    }
  }

  fun updateBnvVisible(isCheck: Boolean) = intent {
    reduce {
      state.copy(isBottomNavigationBarVisible = isCheck)
    }
  }

  fun navigateSaveLink() = intent {
    postSideEffect(MainSideEffect.NavigateSaveLink)
  }
}
