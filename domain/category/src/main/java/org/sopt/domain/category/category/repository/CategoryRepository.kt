package org.sopt.domain.category.category.repository

import org.sopt.model.category.CategoryList

interface CategoryRepository {
  suspend fun getCategoryAll(): Result<CategoryList>

  suspend fun postAddCategory(categoryTitle: String): Result<Int>
}
