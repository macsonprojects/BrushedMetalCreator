package com.example.brushedmetalcreator.di

import android.content.Context
import com.example.brushedmetalcreator.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
// Hilt calls these functions during the build process, but the IDE sees them as unused
@Suppress("unused")
object DataModule {

    @Provides
    @Singleton
    fun provideUserSettingsRepository(
        @ApplicationContext context: Context
    ): UserSettingsRepository {
        return UserSettingsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideImageExportRepository(
        @ApplicationContext context: Context
    ): ImageExportRepository {
        return ImageExportRepositoryImpl(context)
    }
}
