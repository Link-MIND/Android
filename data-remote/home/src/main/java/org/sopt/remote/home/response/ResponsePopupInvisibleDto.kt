package org.sopt.remote.home.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.sopt.model.home.PopupInvisible

@Serializable
data class ResponsePopupInvisibleDto(
  @SerialName("popupId")
  val popupId: Int,
  @SerialName("hideUntil")
  val hideUntil: String,
)

internal fun ResponsePopupInvisibleDto.toCoreModel() = PopupInvisible(
  popupId = popupId,
  popupHideUntil = hideUntil,
)
