package org.sopt.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import org.sopt.datastore.datastore.SecurityDataStore
import org.sopt.home.model.UpdatePriority
import org.sopt.home.usecase.GetMainPageUserClip
import org.sopt.home.usecase.GetPopupInfo
import org.sopt.home.usecase.GetRecentSavedLink
import org.sopt.home.usecase.GetRecommendSite
import org.sopt.home.usecase.GetWeekBestLink
import org.sopt.home.usecase.PatchPopupInvisible
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val getMainPageUserClip: GetMainPageUserClip,
  private val getRecommendSite: GetRecommendSite,
  private val getWeekBestLink: GetWeekBestLink,
  private val getRecentSavedLink: GetRecentSavedLink,
  private val patchPopupInvisible: PatchPopupInvisible,
  private val getPopupInfo: GetPopupInfo,
  private val dataStore: SecurityDataStore,
) : ContainerHost<HomeState, HomeSideEffect>, ViewModel() {
  override val container: Container<HomeState, HomeSideEffect> =
    container(HomeState())

  fun getMainPageUserClip() = intent {
    getMainPageUserClip.invoke().onSuccess {
      reduce {
        state.copy(
          nickName = it.nickName,
          allToastNum = it.allToastNum,
          readToastNum = it.readToastNum,
        )
      }
    }.onFailure {
      Log.d("MainUser", "$it")
    }
  }

  fun getRecentSavedClip() = intent {
    getRecentSavedLink.invoke().onSuccess {
      if (it.isEmpty()) {
        reduce {
          state.copy(recentSavedLink = listOf(null))
        }
      } else {
        reduce {
          state.copy(recentSavedLink = (container.stateFlow.value.recentSavedLink + it).distinctBy { it?.toastId })
        }
      }
    }.onFailure {
      Log.d("RecentSaved", "$it")
    }
  }

  fun showThenHide(showDelay: Long = 500, duration: Long = 2000) = intent {
    runCatching {
      val booleanListFlow = dataStore.flowTooltip().first().toString()
      val stringValue = booleanListFlow.split(",").map { it.toBoolean() }
      if (stringValue.getOrElse(1) { true }) {
        delay(showDelay)
        reduce {
          state.copy(visibleBubbleMark = true)
        }
        delay(duration)
        reduce {
          state.copy(visibleBubbleMark = false)
        }
        dataStore.setTooltip(
          listOf(
            true,
            false,
            stringValue.getOrElse(2) { true },
            stringValue.getOrElse(3) { true },
          ).joinToString(","),
        )
      }
    }
  }

  fun getRecommendSite() = intent {
    getRecommendSite.invoke().onSuccess {
      reduce {
        state.copy(recommendLink = (container.stateFlow.value.recommendLink + it).distinctBy { it.siteId })
      }
    }.onFailure {
      Log.d("Recommend", "$it")
    }
  }

  fun getWeekBestLink() = intent {
    getWeekBestLink.invoke().onSuccess {
      reduce {
        state.copy(weekBestLink = (it).distinctBy { it.toastId })
      }
    }.onFailure {
      Log.d("getWeekBestLink", "$it")
    }
  }

  fun getPopupListInfo() = intent {
    if (dataStore.flowPopupVisibility().first()) {
      getPopupInfo.invoke().onSuccess {
        postSideEffect(HomeSideEffect.ShowPopupInfo)
        reduce {
          state.copy(popupList = it)
        }
      }.onFailure {
        Log.d("getPopupListInfo", "$it")
      }
    }
  }

  fun patchPopupInvisible(popupId: Long, hideDate: Long) {
    viewModelScope.launch {
      patchPopupInvisible.invoke(popupId, hideDate)
        .onSuccess {
          Log.d("patchPopupInvisible", "$it")
        }
        .onFailure {
          Log.d("patchPopupInvisible", "$it")
        }
    }
  }

  fun checkMarketUpdateState() {
    viewModelScope.launch {
      if (dataStore.flowMarketUpdate().first()) {
        val appUpdateManager = AppUpdateManagerFactory.create(context)
        val appUpdateTask = appUpdateManager.appUpdateInfo

        appUpdateTask.addOnSuccessListener { appUpdateInfo ->
          if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
            intent {
              postSideEffect(HomeSideEffect.ShowUpdateDialog)
              reduce {
                state.copy(
                  marketUpdate = UpdatePriority.toUpdatePriority(
                    appUpdateInfo.updatePriority(),
                  ),
                )
              }
            }
          }
//          setToolTip()
        }.addOnFailureListener { appUpdateInfo ->
          Log.d("appUpdateInfo", appUpdateInfo.message.toString())
        }
      }
    }
  }

  fun setToolTip() = viewModelScope.launch {
    dataStore.setTooltip(listOf(true, true, true, true).joinToString(","))
  }

  fun navigateSetting() = intent { postSideEffect(HomeSideEffect.NavigateSetting) }
  fun navigateSaveLink() = intent { postSideEffect(HomeSideEffect.NavigateSaveLink) }
  fun navigateAllClip() = intent { postSideEffect(HomeSideEffect.NavigateAllClip) }

  @OptIn(OrbitExperimental::class)
  fun navigateWebview(url: String, isRead: Boolean = false, toastId: Long = 0) = blockingIntent {
    reduce { state.copy(url = url, toastId = toastId, isRead = isRead) }
    Log.d("testSak", "$toastId $isRead")
    postSideEffect(HomeSideEffect.NavigateWebView)
  }

  fun setPopupVisible() {
    viewModelScope.launch {
      dataStore.setPopupVisibility(false)
    }
  }

  fun setMarketUpdateVisible() {
    viewModelScope.launch {
      dataStore.setMarketUpdate(false)
    }
  }

  fun checkPopupDate(activeStartDate: String, activeEndDate: String): Boolean {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = Calendar.getInstance().time
    val startDate = dateFormat.parse(activeStartDate)
    val endDate = dateFormat.parse(activeEndDate)
    return today.after(startDate) && today.before(endDate) || today == startDate || today == endDate
  }
}
