package com.imagescore.domain.ui.score.model

data class ImageScore(
    val id: Long,
    val imagePath: String,
    val title: String,
    val score: Int,
    val details: ImageScoreDetails
)

data class ImageScoreDetails(
    val date: Long,
    val storageSize: Long,
    val height: Long,
    val width: Long,
    val fileFormat: FileFormat
)

enum class FileFormat {
    JPEG,
    PNG
}