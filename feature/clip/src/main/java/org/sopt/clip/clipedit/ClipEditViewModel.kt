package org.sopt.clip.clipedit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.sopt.domain.category.category.usecase.DeleteCategoryUseCase
import org.sopt.domain.category.category.usecase.GetCategoryAllUseCase
import org.sopt.domain.category.category.usecase.PatchCategoryEditPriorityUseCase
import org.sopt.domain.category.category.usecase.PatchCategoryEditTitleUseCase
import org.sopt.model.category.Category
import org.sopt.ui.view.UiState
import javax.inject.Inject

@HiltViewModel
class ClipEditViewModel @Inject constructor(
  private val getCategoryAll: GetCategoryAllUseCase,
  private val deleteCategory: DeleteCategoryUseCase,
  private val patchCategoryEditTitle: PatchCategoryEditTitleUseCase,
  private val patchCategoryEditPriority: PatchCategoryEditPriorityUseCase,
) : ViewModel() {
  private val _categoryState = MutableStateFlow<UiState<List<Category>>>(UiState.Empty)
  val categoryState: StateFlow<UiState<List<Category>>> = _categoryState.asStateFlow()

  private val _categoryDeleteState = MutableStateFlow<UiState<Int>>(UiState.Empty)
  val categoryDeleteState: StateFlow<UiState<Int>> = _categoryDeleteState.asStateFlow()

  private val _editTitleState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
  val editTitleState: StateFlow<UiState<Unit>> = _editTitleState.asStateFlow()

  private val _categoryEditPriorityState = MutableStateFlow<UiState<Pair<Long, Int>>>(UiState.Empty)
  val categoryEditPriorityState: StateFlow<UiState<Pair<Long, Int>>> = _categoryEditPriorityState.asStateFlow()

  init {
    getCategoryAll()
  }

  fun updateCategoryEditPriorityState(clipId: Long, newIndex: Int) = viewModelScope.launch {
    _categoryEditPriorityState.emit(UiState.Success(Pair(clipId, newIndex)))
  }

  fun getCategoryAll() = viewModelScope.launch {
    getCategoryAll.invoke().onSuccess {
      val allCategoryList = listOf<Category>(
        Category(0, "전체 클립", it.toastNumberInEntire),
      )
      _categoryState.emit(UiState.Success(allCategoryList + it.categories))
    }.onFailure {
      Log.e("실패", it.message.toString())
    }
  }

  fun deleteCategory(deleteCategoryList: Long) = viewModelScope.launch {
    deleteCategory.invoke(param = DeleteCategoryUseCase.Param(deleteCategoryList)).onSuccess {
      _categoryDeleteState.emit(UiState.Success(200))
      getCategoryAll()
    }.onFailure {
      Log.d("카테 삭제-함수안", "실패 $it")
    }
  }

  fun patchCategoryEditTitle(categoryId: Long, newTitle: String) = viewModelScope.launch {
    patchCategoryEditTitle.invoke(param = PatchCategoryEditTitleUseCase.Param(categoryId, newTitle)).onSuccess {
      _editTitleState.emit(UiState.Success(Unit))
    }.onFailure {
      Log.d("카테 이름 수정", "실패 $it")
    }
  }

  fun patchCategoryEditPriority(categoryId: Long, newPriority: Int) = viewModelScope.launch {
    patchCategoryEditPriority.invoke(param = PatchCategoryEditPriorityUseCase.Param(categoryId, newPriority)).onSuccess {
      getCategoryAll()
    }.onFailure {
      Log.d("순위 변경", "실패 $it")
    }
  }
}
