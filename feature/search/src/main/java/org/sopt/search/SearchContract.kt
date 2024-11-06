package org.sopt.search

import org.sopt.model.category.Category
import org.sopt.model.category.Toast

data class SearchState(
  val searchedToasts: List<Toast> = emptyList(),
  val searchedClip: List<Category> = emptyList(),
  val query: String = "",
  val isEmpty: Boolean = false,
)

sealed interface SearchSideEffect {
  data class NavigateToClip(val id: Long, val title: String) : SearchSideEffect
  data class NavigateToWebView(val url: String, val id: Long, val isRead: Boolean) : SearchSideEffect
}
