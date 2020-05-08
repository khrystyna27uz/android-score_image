package com.imagescore.data.ui.score.repo

import androidx.room.Database
import androidx.room.RoomDatabase
import com.imagescore.data.ui.score.dao.ImageScoreDao
import com.imagescore.data.ui.score.model.ImageScoreEntity
import io.reactivex.Completable

@Database(
        entities = [
            ImageScoreEntity::class
        ], version = 1, exportSchema = false
)
abstract class ImageScoreRoomDatabase : RoomDatabase() {
    abstract fun imageScoreDao(): ImageScoreDao

    fun clean(): Completable =
            Completable.fromCallable {
                clearAllTables()
            }
}
