package org.sopt.maincontainer

data class MainState(
  val isBottomNavigationBarVisible: Boolean = true,
  val clipboard: String = "",
  val visibleBubbleMark: Boolean = false,
)

sealed interface MainSideEffect {
  data object NavigateSaveLink : MainSideEffect
}
