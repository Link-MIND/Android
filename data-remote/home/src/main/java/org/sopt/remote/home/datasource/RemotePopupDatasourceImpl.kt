package org.sopt.remote.home.datasource

import org.sopt.home.datasource.RemotePopupDataSource
import org.sopt.model.home.PopupInfo
import org.sopt.model.home.PopupInvisible
import org.sopt.remote.home.api.PopupService
import org.sopt.remote.home.request.RequestPopupInvisibleDto
import org.sopt.remote.home.response.toCoreModel
import javax.inject.Inject

class RemotePopupDatasourceImpl @Inject constructor(
  private val popupService: PopupService,
) : RemotePopupDataSource {
  override suspend fun patchPopupInvisible(popupId: Long, hideDate: Long): PopupInvisible =
    popupService.patchPopupInvisible(RequestPopupInvisibleDto(popupId, hideDate)).data!!.toCoreModel()

  override suspend fun getPopupInfo(): List<PopupInfo> =
    popupService.getPopupInfo().data!!.popupList.map { it.toCoreModel() }
}
