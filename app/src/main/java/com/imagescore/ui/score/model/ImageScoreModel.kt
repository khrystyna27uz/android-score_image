package com.imagescore.ui.score.model

import com.imagescore.domain.ui.score.model.FileFormat
import com.imagescore.domain.ui.score.model.ImageScore

data class ImageScoreModel(
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

fun ImageScoreModel.toDomain() = ImageScore(
    id,
    imagePath,
    title,
    score,
    details.toDomain(),
    location.toDomain()
)


fun ImageScoreDetails.toDomain() =
    com.imagescore.domain.ui.score.model.ImageScoreDetails(
        date, storageSize, height, width,
        fileFormat
    )

fun Location.toDomain() =
    com.imagescore.domain.ui.score.model.Location(
        locationLatitude, locationLongitude
    )

fun ImageScore.toUI() =
    ImageScoreModel(id, imagePath, title, score, details.toUI(), location.toUI())

fun com.imagescore.domain.ui.score.model.ImageScoreDetails.toUI() = ImageScoreDetails(
    date, storageSize, height, width, fileFormat
)

fun com.imagescore.domain.ui.score.model.Location.toUI() = Location(
    locationLatitude, locationLongitude
)

fun List<ImageScore>.toUi(): List<ImageScoreModel> {
    return map {
        it.toUI()
    }
}


