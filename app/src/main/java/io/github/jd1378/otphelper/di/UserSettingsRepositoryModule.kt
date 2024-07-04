package io.github.jd1378.otphelper.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.repository.UserSettingsRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserSettingsRepositoryModule {
  @Binds
  @Singleton
  abstract fun provideUserSettingsRepository(
      userSettingsRepository: UserSettingsRepositoryImpl
  ): UserSettingsRepository
}
