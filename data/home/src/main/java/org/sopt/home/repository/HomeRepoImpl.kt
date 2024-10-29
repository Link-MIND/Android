package org.sopt.home.repository

import org.sopt.home.datasource.RemoteHomeDataSource
import org.sopt.model.home.MainPageData
import org.sopt.model.home.RecentSavedLink
import org.sopt.model.home.RecommendLink
import org.sopt.model.home.WeekBestLink
import javax.inject.Inject

class HomeRepoImpl @Inject constructor(
  private val remoteHomeDataSource: RemoteHomeDataSource,
) : HomeRepository {
  override suspend fun getMainPageUserClip(): Result<MainPageData> =
    runCatching { remoteHomeDataSource.getMainPageUserClip() }

  override suspend fun getRecommendSite(): Result<List<RecommendLink>> =
    runCatching { remoteHomeDataSource.getRecommendSite() }

  override suspend fun getWeekBestLink(): Result<List<WeekBestLink>> =
    runCatching { remoteHomeDataSource.getWeekBestLink() }

  override suspend fun getRecentSavedLink(): Result<List<RecentSavedLink>> =
    runCatching { remoteHomeDataSource.getRecentSavedLink() }
}
