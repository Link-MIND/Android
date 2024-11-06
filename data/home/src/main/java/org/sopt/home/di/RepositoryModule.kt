package org.sopt.home.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.sopt.home.repository.HomeRepoImpl
import org.sopt.home.repository.HomeRepository
import org.sopt.home.repository.PopupRepoImpl
import org.sopt.home.repository.PopupRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
  @Singleton
  @Binds
  abstract fun bindHomeRepository(
    homeRepoImpl: HomeRepoImpl,
  ): HomeRepository

  @Singleton
  @Binds
  abstract fun bindPopupRepository(
    popupRepoImpl: PopupRepoImpl,
  ): PopupRepository
}
