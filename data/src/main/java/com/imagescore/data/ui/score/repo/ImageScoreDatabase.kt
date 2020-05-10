package com.imagescore.data.ui.score.repo

class ImageScoreDatabase(private val configProvider: DBConfigurationProvider) {
    fun build(): ImageScoreRoomDatabase = configProvider.provideBuilder().build()
}