package org.sopt.model.home

data class RecentSavedLink(
  val toastId: Long,
  val toastTitle: String,
  val linkUrl: String,
  val isRead: Boolean,
  val categoryTitle: String?,
  val thumbnailUrl: String?,
)
