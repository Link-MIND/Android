package org.sopt.remote.home.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.sopt.model.home.RecentSavedLink

@Serializable
data class ResponseRecentSavedLinkDto(
  @SerialName("toastId")
  val toastId: Long,
  @SerialName("toastTitle")
  val toastTitle: String,
  @SerialName("linkUrl")
  val linkUrl: String,
  @SerialName("isRead")
  val isRead: Boolean,
  @SerialName("categoryTitle")
  val categoryTitle: String?,
  @SerialName("thumbnailUrl")
  val thumbnailUrl: String?,
)

internal fun ResponseRecentSavedLinkDto.toCoreModel() = RecentSavedLink(
  toastId = toastId,
  toastTitle = toastTitle,
  linkUrl = linkUrl,
  isRead = isRead,
  categoryTitle = categoryTitle,
  thumbnailUrl = thumbnailUrl,
)
