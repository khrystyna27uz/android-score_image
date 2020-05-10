package com.imagescore.data.ui.score.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_scores")
data class ImageScoreEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "image_path") val imagePath: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "score") val score: Int,
    @Embedded(prefix = "details") val details: ImageScoreDetailsEntity,
    @Embedded(prefix = "location") val location: LocationEntity

)

data class ImageScoreDetailsEntity(
    val date: Long,
    val storageSize: Long,
    val height: Long,
    val width: Long,
    val fileFormat: String
)

data class LocationEntity(
    val locationLatitude: Double,
    val locationLongitude: Double
)
