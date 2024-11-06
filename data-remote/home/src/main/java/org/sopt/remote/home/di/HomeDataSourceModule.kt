package org.sopt.remote.home.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.sopt.home.datasource.RemoteHomeDataSource
import org.sopt.home.datasource.RemotePopupDataSource
import org.sopt.remote.home.datasource.RemoteHomeDataSourceImpl
import org.sopt.remote.home.datasource.RemotePopupDatasourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeDataSourceModule {
  @Singleton
  @Binds
  abstract fun bindRemoteHomeDatasource(
    remoteHomeDataSourceImpl: RemoteHomeDataSourceImpl,
  ): RemoteHomeDataSource

  @Singleton
  @Binds
  abstract fun bindRemotePopupDataSource(
    remotePopupDataSourceImpl: RemotePopupDatasourceImpl,
  ): RemotePopupDataSource
}
