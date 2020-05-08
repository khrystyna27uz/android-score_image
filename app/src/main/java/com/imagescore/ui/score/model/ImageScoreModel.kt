package com.imagescore.ui.score.model

import com.imagescore.domain.model.FileFormat
import com.imagescore.domain.model.ImageScore

data class ImageScoreModel(
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


fun ImageScoreModel.toDomain() = ImageScore(id, imagePath, title, score, details.toDomain())


fun ImageScoreDetails.toDomain() = com.imagescore.domain.model.ImageScoreDetails(
    date, storageSize, height, width,
    fileFormat
)

fun ImageScore.toUI() = ImageScoreModel(id, imagePath, title, score, details.toUI())

fun com.imagescore.domain.model.ImageScoreDetails.toUI() = ImageScoreDetails(
    date, storageSize, height, width, fileFormat
)

fun List<ImageScore>.toUi(): List<ImageScoreModel> {
    return map {
        it.toUI()
    }
}


