package org.sopt.data.link.datasource

interface RemoteLinkDataSource {
  suspend fun postSaveLink(linkUrl: String, categoryId: Long?): Int
  suspend fun deleteLink(toastId: Long): Int
  suspend fun patchReadLink(toastId: Long, isRead: Boolean): Boolean
  suspend fun patchLinkTitle(toastId: Long, title: String): String
  suspend fun patchLinkCategory(toastId: Long, categoryId: Long): Long
}
