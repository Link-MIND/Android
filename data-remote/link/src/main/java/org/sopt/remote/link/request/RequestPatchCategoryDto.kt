package org.sopt.remote.link.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPatchCategoryDto(
  @SerialName("toastId")
  val toastId: Long,
  @SerialName("categoryId")
  val categoryId: Long,
)
