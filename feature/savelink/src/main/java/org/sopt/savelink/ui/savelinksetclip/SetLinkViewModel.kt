package org.sopt.savelink.ui.savelinksetclip

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import org.sopt.domain.category.category.usecase.GetCategoryAllUseCase
import org.sopt.domain.category.category.usecase.GetCategoryDuplicateUseCase
import org.sopt.domain.category.category.usecase.PostAddCategoryTitleUseCase
import org.sopt.domain.link.usecase.PostSaveLinkUseCase
import org.sopt.savelink.ui.model.Clip
import org.sopt.savelink.ui.model.toModel
import javax.inject.Inject

@HiltViewModel
class SetLinkViewModel @Inject constructor(
  private val saveLinkUseCase: PostSaveLinkUseCase,
  private val getCategoryAllUseCase: GetCategoryAllUseCase,
  private val postAddCategoryTitle: PostAddCategoryTitleUseCase,
  private val getCategoryDuplicateUseCase: GetCategoryDuplicateUseCase,
) : ContainerHost<SaveLinkSetClipState, SaveLinkSetClipSideEffect>, ViewModel() {
  override val container: Container<SaveLinkSetClipState, SaveLinkSetClipSideEffect> =
    container(SaveLinkSetClipState())

  fun getCategoryAll() = intent {
    getCategoryAllUseCase().onSuccess {
      reduce {
        state.copy(
          allClipCountNum = it.toastNumberInEntire,
          categoryList = (
            listOf(
              Clip(
                null,
                "전체 클립",
                it.toastNumberInEntire,
                true,
              ),
            ) + container.stateFlow.value.categoryList + it.categories.map { it.toModel() }
            ).distinctBy { it.categoryId },
        )
      }
    }.onFailure {
      Log.d("getCategortFail", "$it")
    }
  }

  fun getCategoryDuplicate(title: String) = intent {
    getCategoryDuplicateUseCase.invoke(param = GetCategoryDuplicateUseCase.Param(title))
      .onSuccess {
        reduce {
          state.copy(
            duplicate = it.isDuplicate,
          )
        }
        if (!it.isDuplicate) saveCategoryTitle(title)
      }.onFailure {
        Log.d("카테 중복 체크", "실패 $it")
      }
  }

  fun updateDuplicate() = intent {
    reduce {
      state.copy(
        duplicate = false,
      )
    }
  }

  fun saveCategoryTitle(categoryTitle: String) = viewModelScope.launch {
    postAddCategoryTitle(
      PostAddCategoryTitleUseCase.Param(
        categoryTitle = categoryTitle,
      ),
    ).onSuccess {
      getCategoryAll()
    }.onFailure {
      showSnackBarError()
    }
  }

  private val mutex = Mutex()
  fun saveLink(linkUrl: String, categoryId: Long?, func: () -> Unit) = viewModelScope.launch {
    if (!mutex.isLocked) {
      mutex.withLock {
        func()
        saveLinkUseCase(
          PostSaveLinkUseCase.Param(
            linkUrl = linkUrl,
            categoryId = if (categoryId == 0.toLong()) null else categoryId,
          ),
        ).onSuccess {
          navigateSetLink()
          if (it != 201) showSnackBar()
        }.onFailure {
          showSnackBar()
        }
      }
    }
  }

  fun updateCategoryId(categoryId: Long?) = intent {
    reduce {
      state.copy(categoryId = categoryId)
    }
  }

  fun updateUrl(url: String) = intent {
    reduce {
      state.copy(url = url)
    }
  }

  private fun navigateSetLink() = intent { postSideEffect(SaveLinkSetClipSideEffect.NavigateSaveLinkSetClip) }
  fun navigateUp() = intent { postSideEffect(SaveLinkSetClipSideEffect.NavigateUp) }
  fun showBottomSheet() = intent { postSideEffect(SaveLinkSetClipSideEffect.ShowBottomSheet) }
  fun showDialog() = intent { postSideEffect(SaveLinkSetClipSideEffect.ShowDialog) }

  private fun showSnackBar() = intent { postSideEffect(SaveLinkSetClipSideEffect.ShowSnackBar) }
  private fun showSnackBarError() = intent { postSideEffect(SaveLinkSetClipSideEffect.ShowSnackBarError) }
}
