package org.sopt.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import org.sopt.domain.category.category.usecase.GetSearchResultUserCase
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
  private val getSearchResultUserCase: GetSearchResultUserCase,
) : ContainerHost<SearchState, SearchSideEffect>, ViewModel() {
  override val container: Container<SearchState, SearchSideEffect> =
    container(SearchState())

  init {
    viewModelScope.launch {
      container.stateFlow.debounce(300).collectLatest {
        getSearchResult(it.query)
      }
    }
  }

  fun getSearchResult(query: String) = intent {
    getSearchResultUserCase(query).onSuccess {
      if (it.categories.isNullOrEmpty() && it.toasts.isNullOrEmpty()) {
        reduce { state.copy(searchedToasts = emptyList(), searchedClip = emptyList(), isEmpty = true) }
      } else {
        reduce { state.copy(searchedToasts = it.toasts.orEmpty(), searchedClip = it.categories.orEmpty(), isEmpty = false) }
      }
    }.onFailure {
      reduce { state.copy(searchedToasts = emptyList(), searchedClip = emptyList(), isEmpty = true) }
    }
  }

  fun updateQuery(query: String) = intent {
    reduce { state.copy(query = query) }
  }

  fun clear() = intent {
    reduce { state.copy(emptyList(), emptyList(), "", true) }
  }

  fun navigateToWebView(url: String, linkId: Long, isRead: Boolean) = intent {
    postSideEffect(SearchSideEffect.NavigateToWebView(url, linkId, isRead))
  }

  fun navigateToClip(clipId: Long, title: String) = intent {
    postSideEffect(SearchSideEffect.NavigateToClip(clipId, title))
  }
}
