package org.sopt.remote.home.api

import org.sopt.network.model.response.base.BaseResponse
import org.sopt.remote.home.request.RequestPopupInvisibleDto
import org.sopt.remote.home.response.ResponsePopupInfoDto
import org.sopt.remote.home.response.ResponsePopupInvisibleDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface PopupService {
  companion object {
    const val API = "api"
    const val V2 = "v2"
    const val POPUP = "popup"
  }

  @PATCH("/$API/$V2/$POPUP")
  suspend fun patchPopupInvisible(
    @Body requestPopupInvisibleDto: RequestPopupInvisibleDto,
  ): BaseResponse<ResponsePopupInvisibleDto>

  @GET("/$API/$V2/$POPUP")
  suspend fun getPopupInfo(): BaseResponse<ResponsePopupInfoDto>
}
