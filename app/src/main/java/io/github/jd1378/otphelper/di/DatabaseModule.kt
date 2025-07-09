package io.github.jd1378.otphelper.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jd1378.otphelper.data.local.db.OtpHelperDatabase
import javax.inject.Singleton

private const val DATABASE_FILE_NAME = "appdata.db"

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

  @Provides
  @Singleton
  fun provideOtpHelperDatabase(@ApplicationContext context: Context): OtpHelperDatabase {
    return Room.databaseBuilder(
            context,
            OtpHelperDatabase::class.java,
            DATABASE_FILE_NAME,
        )
        .fallbackToDestructiveMigration(true)
        .build()
  }
}
