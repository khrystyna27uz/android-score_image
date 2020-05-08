package com.imagescore.data.ui.score.repo

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.imagescore.data.ui.score.repo.ImageScoreRoomDatabase

const val DB_NAME = "imagescore-db"

class DBConfigurationProvider(private val appContext: Context) {

    fun provideBuilder(): RoomDatabase.Builder<ImageScoreRoomDatabase> =
            provideDebug()

    private fun createBuilder(): RoomDatabase.Builder<ImageScoreRoomDatabase> =
        Room.databaseBuilder(appContext, ImageScoreRoomDatabase::class.java, DB_NAME)

    private fun provideDebug(): RoomDatabase.Builder<ImageScoreRoomDatabase> =
        createBuilder().fallbackToDestructiveMigration()
}