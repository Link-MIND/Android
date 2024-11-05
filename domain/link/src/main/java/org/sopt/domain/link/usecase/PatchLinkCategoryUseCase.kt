package org.sopt.domain.link.usecase

import org.sopt.domain.link.repository.LinkRepository
import javax.inject.Inject

class PatchLinkCategoryUseCase @Inject constructor(
  private val linkRepository: LinkRepository,
) {
  suspend operator fun invoke(param: Param): Result<Long> = linkRepository.patchToastCategory(
    toastId = param.toastId,
    categoryId = param.categoryId,
  )

  data class Param(
    val toastId: Long,
    val categoryId: Long,
  )
}
