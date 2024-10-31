package org.sopt.home

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import org.sopt.home.usecase.GetMainPageUserClip
import org.sopt.home.usecase.GetRecentSavedLink
import org.sopt.home.usecase.GetRecommendSite
import org.sopt.home.usecase.GetWeekBestLink
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val getMainPageUserClip: GetMainPageUserClip,
  private val getRecommendSite: GetRecommendSite,
  private val getWeekBestLink: GetWeekBestLink,
  private val getRecentSavedLink: GetRecentSavedLink,
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

  fun navigateSearch() = intent { postSideEffect(HomeSideEffect.NavigateSearch) }
  fun navigateSetting() = intent { postSideEffect(HomeSideEffect.NavigateSetting) }
  fun navigateSaveLink() = intent { postSideEffect(HomeSideEffect.NavigateSaveLink) }
  fun navigateAllClip() = intent { postSideEffect(HomeSideEffect.NavigateAllClip) }

  @OptIn(OrbitExperimental::class)
  fun navigateWebview(url: String) = blockingIntent {
    reduce { state.copy(url = url) }
    postSideEffect(HomeSideEffect.NavigateWebView)
  }
}
