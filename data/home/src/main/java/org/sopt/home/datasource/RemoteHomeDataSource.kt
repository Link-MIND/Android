package org.sopt.home.datasource

import org.sopt.model.home.MainPageData
import org.sopt.model.home.RecentSavedLink
import org.sopt.model.home.RecommendLink
import org.sopt.model.home.WeekBestLink

interface RemoteHomeDataSource {
  suspend fun getMainPageUserClip(): MainPageData
  suspend fun getRecommendSite(): List<RecommendLink>
  suspend fun getWeekBestLink(): List<WeekBestLink>
  suspend fun getRecentSavedLink(): List<RecentSavedLink>
}
