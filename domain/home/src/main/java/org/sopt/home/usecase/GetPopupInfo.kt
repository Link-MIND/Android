package org.sopt.home.usecase

import org.sopt.home.repository.PopupRepository
import org.sopt.model.home.PopupInfo
import javax.inject.Inject

class GetPopupInfo @Inject constructor(
  private val popupRepository: PopupRepository,
) {
  suspend operator fun invoke(): Result<List<PopupInfo>> = popupRepository.getPopupInfo()
}
