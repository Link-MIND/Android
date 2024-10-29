package org.sopt.home.usecase

import org.sopt.home.repository.HomeRepository
import org.sopt.model.home.RecentSavedLink
import javax.inject.Inject

class GetRecentSavedLink @Inject constructor(
  private val homeRepository: HomeRepository,
) {
  suspend operator fun invoke(): Result<List<RecentSavedLink>> = homeRepository.getRecentSavedLink()
}
