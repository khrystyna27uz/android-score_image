package com.imagescore.data.ui.score.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.imagescore.data.ui.score.model.ImageScoreEntity
import io.reactivex.Observable

@Dao
interface ImageScoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(wellEntity: ImageScoreEntity)

    @Query("SELECT * FROM image_scores")
    fun getAll(): Observable<List<ImageScoreEntity>>

    @Query("DELETE FROM image_scores")
    fun delete()

    @Query("SELECT * FROM image_scores WHERE id = :id")
    fun findImageScoreById(id: Long): Observable<ImageScoreEntity>
}
