package org.sopt.remote.link.api

import org.sopt.network.model.response.base.BaseResponse
import org.sopt.remote.link.request.RequestIsReadDto
import org.sopt.remote.link.request.RequestPatchCategoryDto
import org.sopt.remote.link.request.RequestPatchTitleDto
import org.sopt.remote.link.request.RequestWriteDto
import org.sopt.remote.link.response.ResponseIsReadDto
import org.sopt.remote.link.response.ResponsePatchCategoryDto
import org.sopt.remote.link.response.ResponsePatchTitleDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface LinkService {
  companion object {
    const val TOAST = "toast"
    const val DELETE = "delete"
    const val ISREAD = "is-read"
    const val SAVE = "save"
    const val TITLE = "title"
    const val CATEGORY = "category"
  }

  @POST("/$TOAST/$SAVE")
  suspend fun postLink(
    @Body requestWriteDto: RequestWriteDto,
  ): BaseResponse<Unit>

  @DELETE("/$TOAST/$DELETE")
  suspend fun deleteLink(@Query("toastId") toastId: Long): BaseResponse<Unit>

  @PATCH("/$TOAST/$ISREAD")
  suspend fun patchLink(@Body requestIsReadDto: RequestIsReadDto): BaseResponse<ResponseIsReadDto>

  @PATCH("/$TOAST/$TITLE")
  suspend fun patchLinkTitle(@Body requestPatchTitleDto: RequestPatchTitleDto): BaseResponse<ResponsePatchTitleDto>

  @PATCH("/$TOAST/$CATEGORY")
  suspend fun patchToastCategory(@Body requestPatchCategoryDto: RequestPatchCategoryDto): BaseResponse<ResponsePatchCategoryDto>
}
