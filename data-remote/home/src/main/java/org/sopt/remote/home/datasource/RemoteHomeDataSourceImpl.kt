package org.sopt.remote.home.datasource

import org.sopt.home.datasource.RemoteHomeDataSource
import org.sopt.model.home.MainPageData
import org.sopt.model.home.RecentSavedLink
import org.sopt.model.home.RecommendLink
import org.sopt.model.home.WeekBestLink
import org.sopt.remote.home.api.HomeService
import org.sopt.remote.home.response.toCoreModel
import javax.inject.Inject

class RemoteHomeDataSourceImpl @Inject constructor(
  private val homeService: HomeService,
) : RemoteHomeDataSource {
  override suspend fun getMainPageUserClip(): MainPageData =
    homeService.getMainUserClip().data!!.toCoreModel()

  override suspend fun getRecommendSite(): List<RecommendLink> =
    homeService.getRecommendSites().data!!.map { it.toCoreModel() }

  override suspend fun getWeekBestLink(): List<WeekBestLink> =
    homeService.getWeekBestLink().data!!.map { it.toCoreModel() }

  override suspend fun getRecentSavedLink(): List<RecentSavedLink> =
    homeService.getRecentSavedLink().data!!.map { it.toCoreModel() }
}
