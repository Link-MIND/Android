package org.sopt.home.datasource

import org.sopt.model.home.PopupInfo
import org.sopt.model.home.PopupInvisible

interface RemotePopupDataSource {
  suspend fun patchPopupInvisible(popupId: Long, hideDate: Long): PopupInvisible
  suspend fun getPopupInfo(): List<PopupInfo>
}
