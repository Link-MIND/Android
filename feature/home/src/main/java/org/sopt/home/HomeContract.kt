package org.sopt.home

import org.sopt.home.model.UpdatePriority
import org.sopt.model.category.Category
import org.sopt.model.home.PopupInfo
import org.sopt.model.home.RecentSavedLink
import org.sopt.model.home.RecommendLink
import org.sopt.model.home.WeekBestLink

data class HomeState(
  val nickName: String = "",
  val readToastNum: Int = 0,
  val allToastNum: Int = 0,
  val categoryList: List<Category?> = emptyList(),
  val weekBestLink: List<WeekBestLink> = emptyList(),
  val recommendLink: List<RecommendLink> = emptyList(),
  val recentSavedLink: List<RecentSavedLink?> = emptyList(),
  val url: String = "",
  val toastId: Long = 0,
  val isRead: Boolean = false,
  val categoryId: Long? = 0,
  val categoryName: String? = "전체 클립",
  val popupList: List<PopupInfo> = emptyList(),
  val marketUpdate: UpdatePriority = UpdatePriority.EMPTY,
  val visibleBubbleMark: Boolean = false,
) {
  fun calculateProgress(): Int {
    if (readToastNum > allToastNum) return 0
    return if (allToastNum == 0) {
      0
    } else {
      ((readToastNum.toDouble() / allToastNum.toDouble()) * 100).toInt()
    }
  }
}

sealed interface HomeSideEffect {
  data object NavigateSetting : HomeSideEffect
  data object NavigateClipLink : HomeSideEffect
  data object NavigateAllClip : HomeSideEffect
  data object NavigateWebView : HomeSideEffect
  data object ShowPopupInfo : HomeSideEffect
  data object ShowUpdateDialog : HomeSideEffect
  data object NavigateSaveLink : HomeSideEffect
}
