package io.github.jd1378.otphelper.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jd1378.otphelper.repository.IgnoredNotifsRepository
import io.github.jd1378.otphelper.repository.IgnoredNotifsRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IgnoredNotifsRepositoryModule {
  @Binds
  @Singleton
  abstract fun provideIgnoredNotifsRepository(
      ignoredNotifsRepositoryImpl: IgnoredNotifsRepositoryImpl
  ): IgnoredNotifsRepository
}
