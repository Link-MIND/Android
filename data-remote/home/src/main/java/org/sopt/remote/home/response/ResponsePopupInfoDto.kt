package org.sopt.remote.home.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.sopt.model.home.PopupInfo

@Serializable
data class ResponsePopupInfoDto(
  @SerialName("popupList")
  val popupList: List<ResponsePopupInfo>,
)

@Serializable
data class ResponsePopupInfo(
  @SerialName("id")
  val id: Int,
  @SerialName("image")
  val image: String,
  @SerialName("activeStartDate")
  val activeStartDate: String,
  @SerialName("activeEndDate")
  val activeEndDate: String,
  @SerialName("linkUrl")
  val linkUrl: String,
)

internal fun ResponsePopupInfoDto.toCoreModel() = popupList.map {
  it.toCoreModel()
}

internal fun ResponsePopupInfo.toCoreModel() = PopupInfo(
  popupId = id,
  popupImage = image,
  popupActiveStartDate = activeStartDate,
  popupActiveEndDate = activeEndDate,
  popupLinkUrl = linkUrl,
)
