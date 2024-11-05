package org.sopt.model.timer

import org.sopt.model.category.Category

data class Clip(
  val id: Long,
  val name: String,
  val count: Long,
  var isSelected: Boolean,
)

fun List<Category>.toUiModel(): List<Clip> = this.map {
  Clip(
    id = it.categoryId ?: 0,
    name = it.categoryTitle ?: "",
    count = it.toastNum,
    isSelected = false,
  )
}
