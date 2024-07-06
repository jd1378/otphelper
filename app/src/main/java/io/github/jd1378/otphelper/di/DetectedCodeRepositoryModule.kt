package io.github.jd1378.otphelper.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jd1378.otphelper.repository.DetectedCodeRepository
import io.github.jd1378.otphelper.repository.DetectedCodeRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DetectedCodeRepositoryModule {
  @Binds
  @Singleton
  abstract fun provideDetectedCodeRepository(
      detectedCodeRepositoryImpl: DetectedCodeRepositoryImpl
  ): DetectedCodeRepository
}
