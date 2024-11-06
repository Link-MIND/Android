package org.sopt.home.repository

import org.sopt.home.datasource.RemotePopupDataSource
import org.sopt.model.home.PopupInfo
import org.sopt.model.home.PopupInvisible
import javax.inject.Inject

class PopupRepoImpl @Inject constructor(
  private val remotePopupDataSource: RemotePopupDataSource,
) : PopupRepository {
  override suspend fun patchPopupInvisible(popupId: Long, hideDate: Long): Result<PopupInvisible> =
    runCatching { remotePopupDataSource.patchPopupInvisible(popupId, hideDate) }

  override suspend fun getPopupInfo(): Result<List<PopupInfo>> =
    runCatching { remotePopupDataSource.getPopupInfo() }
}
