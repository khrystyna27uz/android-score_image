package com.imagescore.domain.ui.score.model

data class ImageScore(
    val id: Long,
    val imagePath: String,
    val title: String,
    val score: Int,
    val details: ImageScoreDetails,
    val location: Location
)

data class ImageScoreDetails(
    val date: Long,
    val storageSize: Long,
    val height: Long,
    val width: Long,
    val fileFormat: FileFormat
)

data class Location(
    val locationLatitude: Double,
    val locationLongitude: Double
)

enum class FileFormat {
    JPEG,
    PNG
}