package org.sopt.remote.home.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPopupInvisibleDto(
  @SerialName("popupId")
  val popupId: Long,
  @SerialName("hideDate")
  val hideDate: Long,
)
