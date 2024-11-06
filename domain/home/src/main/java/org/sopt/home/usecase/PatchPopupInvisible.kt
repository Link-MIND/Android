package org.sopt.home.usecase

import org.sopt.home.repository.PopupRepository
import org.sopt.model.home.PopupInvisible
import javax.inject.Inject

class PatchPopupInvisible @Inject constructor(
  private val popupRepository: PopupRepository,
) {
  suspend operator fun invoke(popupId: Long, hideDate: Long): Result<PopupInvisible> =
    popupRepository.patchPopupInvisible(popupId, hideDate)
}
