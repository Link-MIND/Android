package org.sopt.clip

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.sopt.domain.category.category.usecase.DeleteCategoryUseCase
import org.sopt.domain.category.category.usecase.GetCategoryAllUseCase
import org.sopt.domain.category.category.usecase.GetCategoryDuplicateUseCase
import org.sopt.domain.category.category.usecase.GetCategoryLinkUseCase
import org.sopt.domain.category.category.usecase.PatchCategoryEditPriorityUseCase
import org.sopt.domain.category.category.usecase.PatchCategoryEditTitleUseCase
import org.sopt.domain.category.category.usecase.PostAddCategoryTitleUseCase
import org.sopt.model.category.Category
import org.sopt.model.category.CategoryDuplicate
import org.sopt.model.category.CategoryLink
import org.sopt.ui.view.UiState
import javax.inject.Inject

@HiltViewModel
class ClipViewModel @Inject constructor(
  private val getCategoryAll: GetCategoryAllUseCase,
  private val deleteCategory: DeleteCategoryUseCase,
  private val getCategoryDuplicate: GetCategoryDuplicateUseCase,
  private val getCategoryLink: GetCategoryLinkUseCase,
  private val postAddCategoryTitle: PostAddCategoryTitleUseCase,

  private val patchCategoryEditTitle: PatchCategoryEditTitleUseCase,
  private val patchCategoryEditPriority: PatchCategoryEditPriorityUseCase,
) : ViewModel() {
  private val _categoryState = MutableStateFlow<UiState<List<Category>>>(UiState.Empty)
  val categoryState: StateFlow<UiState<List<Category>>> = _categoryState.asStateFlow()
  var totalClip = Category(categoryId = 0, categoryTitle = "전체 클립", toastNum = 0)

  private val _linkState = MutableStateFlow<UiState<List<CategoryLink>>>(UiState.Empty)
  val linkState: StateFlow<UiState<List<CategoryLink>>> = _linkState.asStateFlow()

  private val _duplicateState = MutableStateFlow<UiState<CategoryDuplicate>>(UiState.Empty)
  val duplicateState: StateFlow<UiState<CategoryDuplicate>> = _duplicateState.asStateFlow()

  private val _categoryDeleteState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
  val categoryDeleteState: StateFlow<UiState<Unit>> = _categoryDeleteState.asStateFlow()

  private val _editTitleState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
  val editTitleState: StateFlow<UiState<Unit>> = _editTitleState.asStateFlow()

  private val _editPriorityState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
  val editPriorityState: StateFlow<UiState<Unit>> = _editPriorityState.asStateFlow()

  init {
    getCategoryAll()
  }

  var toggleSelectedPast: SelectedToggle = SelectedToggle.ALL

  val mockClipData = listOf<ClipsDTO>(
    ClipsDTO("a", 1, 1),
    ClipsDTO("b", 2, 2),
    ClipsDTO("c", 3, 3),
    ClipsDTO("a", 1, 4),
    ClipsDTO("b", 2, 5),
    ClipsDTO("c", 3, 6),
    ClipsDTO("a", 1, 7),
    ClipsDTO("b", 2, 8),
    ClipsDTO("c", 3, 9),
    ClipsDTO("a", 1, 10),
    ClipsDTO("b", 2, 11),
    ClipsDTO("c", 3, 12),
    ClipsDTO("a", 1, 13),
    ClipsDTO("b", 2, 14),
    ClipsDTO("c", 3, 15),
    ClipsDTO("a", 1, 16),
    ClipsDTO("b", 2, 17),
    ClipsDTO("c", 3, 18),
    ClipsDTO("a", 1, 20),
    ClipsDTO("b", 2, 21),
    ClipsDTO("c", 3, 22),
    ClipsDTO("a", 1, 23),
    ClipsDTO("b", 2, 24),
    ClipsDTO("c", 3, 25),
  )

  val mockLinkData = listOf<LinkDTO>(
    LinkDTO("https://www.daum.net", 12, "제목1", "맛집", 1),
    LinkDTO("www.i.com", 12, "제목2", "맛집2", 2),
    LinkDTO("www.n.com", 12, "제목3", "맛집3", 3),
    LinkDTO("www.k.com", 12, "제목4", "맛집4", 4),
    LinkDTO("www.l.com", 12, "제목1", "맛집", 5),
    LinkDTO("www.i.com", 12, "제목2", "맛집2", 6),
    LinkDTO("www.n.com", 12, "제목3", "맛집3", 7),
    LinkDTO("www.k.com", 12, "제목4", "맛집4", 8),
    LinkDTO("www.l.com", 12, "제목1", "맛집", 9),
    LinkDTO("www.i.com", 12, "제목2", "맛집2", 10),
    LinkDTO("www.n.com", 12, "제목3", "맛집3", 11),
    LinkDTO("www.k.com", 12, "제목4", "맛집4", 12),
  )

  val mockDataListState = MutableLiveData<Boolean>(false)
  fun set(value: Boolean) {
    mockDataListState.value = value
  }

  fun getCategoryAll() = viewModelScope.launch {
    getCategoryAll.invoke().onSuccess {
      Log.d("test", "$it")
      val list: MutableList<Category> = it.categories.toMutableList()
      totalClip.toastNum = it.toastNumberInEntire
      list.add(0, totalClip)
      _categoryState.emit(UiState.Success(list))
    }.onFailure {
      Log.e("실패", it.message.toString())
    }
  }

  fun deleteCategory(deleteCategoryList: List<Long>) = viewModelScope.launch {
    deleteCategory.invoke(param = DeleteCategoryUseCase.Param(deleteCategoryList)).onSuccess {
      _categoryDeleteState.emit(UiState.Success(it))

      Log.d("카테 삭제-함수안", "성공")
    }.onFailure {
      Log.d("카테 삭제-함수안", "실패")
    }
  }

  // 서버 통신 확인 완
  fun getCategoryDuplicate(title: String) = viewModelScope.launch {
    getCategoryDuplicate.invoke(param = GetCategoryDuplicateUseCase.Param(title)).onSuccess {
      _duplicateState.emit(UiState.Success(it))
      Log.d("카테 중복 체크", "$title")
    }.onFailure {
      Log.d("카테 중복 체크", "실패")
    }
  }

  fun getCategoryLink(filter: String?, categoryId: Long?) = viewModelScope.launch {
    getCategoryLink(param = GetCategoryLinkUseCase.Param(filter = filter, categoryId = categoryId)).onSuccess {
      val list: MutableList<CategoryLink> = it.toastListDto.toMutableList()
      _linkState.emit(UiState.Success(list))
      Log.d("카테 안의 링크 검색", "성공")
    }.onFailure {
      Log.d("카테 안의 링크 검색", it.message.toString())
    }
  }

  // 확인 완
  fun postAddCategoryTitle(categoryTitle: String) = viewModelScope.launch {
    postAddCategoryTitle.invoke(param = PostAddCategoryTitleUseCase.Param(categoryTitle)).onSuccess {
      Log.d("카테 추가", "성공 ")
      getCategoryAll()
    }.onFailure {
      Log.d("카테 추가", "실패")
    }
  }

  fun patchCategoryEditTitle(categoryId: Long, newTitle: String) = viewModelScope.launch {
    Log.d("뷰모델", "$newTitle") // string 값 잘 가져옴
    patchCategoryEditTitle.invoke(param = PatchCategoryEditTitleUseCase.Param(categoryId, newTitle)).onSuccess {
      Log.d("카테 이름 수정", "성공 ")
      _editPriorityState.emit(UiState.Success(Unit))
    }.onFailure {
      Log.d("카테 이름 수정", "실패")
      Log.d("들어온 id 타이틀", "$categoryId $newTitle")
    }

    fun patchCategoryEditPriority(categoryId: Long, newPriority: Int) = viewModelScope.launch {
      patchCategoryEditPriority.invoke(param = PatchCategoryEditPriorityUseCase.Param(categoryId, newPriority)).onSuccess {
        Log.d("순위 변경", "성공 ")
        _editTitleState.emit(UiState.Success(Unit))
      }.onFailure {
        Log.d("순위 변경", "실패")
      }
    }
  }
}
