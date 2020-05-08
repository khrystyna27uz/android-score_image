package com.imagescore.data.ui.score.repo

import com.imagescore.data.ui.score.repo.DBConfigurationProvider

class ImageScoreDatabase(private val configProvider: DBConfigurationProvider) {
    fun build(): ImageScoreRoomDatabase = configProvider.provideBuilder().build()
}