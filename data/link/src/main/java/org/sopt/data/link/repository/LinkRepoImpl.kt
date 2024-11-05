package org.sopt.data.link.repository

import org.sopt.data.link.datasource.RemoteLinkDataSource
import org.sopt.domain.link.repository.LinkRepository
import javax.inject.Inject

class LinkRepoImpl @Inject constructor(
  private val remoteCategoryDataSource: RemoteLinkDataSource,
) : LinkRepository {
  override suspend fun postSaveLink(linkUrl: String, categoryId: Long?): Result<Int> =
    runCatching { remoteCategoryDataSource.postSaveLink(linkUrl, categoryId) }

  override suspend fun deleteLink(toastId: Long): Result<Int> =
    runCatching { remoteCategoryDataSource.deleteLink(toastId) }

  override suspend fun patchReadLink(toastId: Long, isRead: Boolean): Result<Boolean> =
    runCatching { remoteCategoryDataSource.patchReadLink(toastId, isRead) }

  override suspend fun patchLinkTitle(toastId: Long, title: String): Result<String> =
    runCatching { remoteCategoryDataSource.patchLinkTitle(toastId, title) }

  override suspend fun patchToastCategory(toastId: Long, categoryId: Long): Result<Long> =
    runCatching { remoteCategoryDataSource.patchLinkCategory(toastId, categoryId) }
}
