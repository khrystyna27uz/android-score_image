package com.imagescore.domain.ui.score.di

import android.content.Context
import com.imagescore.data.ui.score.dao.ImageScoreDao
import com.imagescore.data.ui.score.repo.DBConfigurationProvider
import com.imagescore.data.ui.score.repo.ImageScoreDatabase
import com.imagescore.data.ui.score.repo.ImageScoreRoomDatabase
import com.imagescore.domain.ui.score.usecase.ImageScoreUseCase
import dagger.Module
import dagger.Provides

@Module
class ImageScoreUseCaseModule {

    @Provides
    fun provideImageScoreDatabase(context: Context): ImageScoreRoomDatabase =
        ImageScoreDatabase(
            DBConfigurationProvider(
                context
            )
        ).build()

    @Provides
    fun provideImageScoreDao(database: ImageScoreRoomDatabase): ImageScoreDao {
        return database.imageScoreDao()
    }

    @Provides
    fun provideImageScoreUseCase(dao: ImageScoreDao) =
        ImageScoreUseCase(dao)

}