package org.sopt.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import org.sopt.domain.category.category.usecase.PostAddCategoryTitleUseCase
import org.sopt.home.usecase.GetMainPageUserClip
import org.sopt.home.usecase.GetPopupInfo
import org.sopt.home.usecase.GetRecommendSite
import org.sopt.home.usecase.GetWeekBestLink
import org.sopt.home.usecase.PatchPopupInvisible
import org.sopt.model.category.Category
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val getMainPageUserClip: GetMainPageUserClip,
  private val getRecommendSite: GetRecommendSite,
  private val getWeekBestLink: GetWeekBestLink,
  private val postAddCategoryTitle: PostAddCategoryTitleUseCase,
  private val patchPopupInvisible: PatchPopupInvisible,
  private val getPopupInfo: GetPopupInfo,
) : ContainerHost<HomeState, HomeSideEffect>, ViewModel() {
  override val container: Container<HomeState, HomeSideEffect> =
    container(HomeState())

  fun saveCategoryTitle(categoryTitle: String) = viewModelScope.launch {
    postAddCategoryTitle(
      PostAddCategoryTitleUseCase.Param(
        categoryTitle = categoryTitle,
      ),
    ).onSuccess {
      getMainPageUserClip()
    }.onFailure { Log.d("saveCategoryTitleFail", "$it") }
  }

  fun getMainPageUserClip() = intent {
    getMainPageUserClip.invoke().onSuccess {
      reduce {
        val tempCategoryList = listOf(
          Category(
            0,
            "전체 카테고리",
            it.allToastNum.toLong(),
          ),
        ) + it.mainCategoryDto
        val categoryList = if (tempCategoryList.size < 4) tempCategoryList + null else tempCategoryList
        val finalCategoryList = categoryList.distinctBy { it?.categoryId }
        state.copy(
          nickName = it.nickName,
          allToastNum = it.allToastNum,
          readToastNum = it.readToastNum,
          categoryList = (
            finalCategoryList
            ),
        )
      }
    }.onFailure {
      Log.d("MainUser", "$it")
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
    getPopupInfo.invoke().onSuccess {
      postSideEffect(HomeSideEffect.ShowPopupInfo)
      reduce {
        state.copy(popupList = it)
      }
    }.onFailure {
      Log.d("getPopupListInfo", "$it")
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

  fun navigateSearch() = intent { postSideEffect(HomeSideEffect.NavigateSearch) }
  fun navigateSetting() = intent { postSideEffect(HomeSideEffect.NavigateSetting) }
  fun showBottomSheet() = intent { postSideEffect(HomeSideEffect.ShowBottomSheet) }

  @OptIn(OrbitExperimental::class)
  fun navigateClipLink(categoryId: Long?, categoryName: String?) = blockingIntent {
    reduce {
      state.copy(
        categoryId = categoryId,
        categoryName = categoryName,
      )
    }
    postSideEffect(HomeSideEffect.NavigateClipLink)
  }

  @OptIn(OrbitExperimental::class)
  fun navigateWebview(url: String) = blockingIntent {
    reduce { state.copy(url = url) }
    postSideEffect(HomeSideEffect.NavigateWebView)
  }

  fun checkPopupDate(activeStartDate: String, activeEndDate: String): Boolean {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = Calendar.getInstance().time
    val startDate = dateFormat.parse(activeStartDate)
    val endDate = dateFormat.parse(activeEndDate)
    return today.after(startDate) && today.before(endDate) || today == startDate || today == endDate
  }
}
