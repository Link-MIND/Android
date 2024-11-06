package org.sopt.home.repository

import org.sopt.model.home.PopupInfo
import org.sopt.model.home.PopupInvisible

interface PopupRepository {
  suspend fun patchPopupInvisible(popupId: Long, hideDate: Long): Result<PopupInvisible>
  suspend fun getPopupInfo(): Result<List<PopupInfo>>
}
