package com.imagescore.domain.ui.score.mapper

import com.imagescore.data.ui.score.model.ImageScoreDetailsEntity
import com.imagescore.data.ui.score.model.ImageScoreEntity
import com.imagescore.data.ui.score.model.LocationEntity
import com.imagescore.domain.ui.score.model.FileFormat
import com.imagescore.domain.ui.score.model.ImageScore
import com.imagescore.domain.ui.score.model.ImageScoreDetails
import com.imagescore.domain.ui.score.model.Location

fun ImageScoreEntity.toDomain() =
    ImageScore(
        id,
        imagePath,
        title,
        score,
        ImageScoreDetails(
            details.date,
            details.storageSize,
            details.height,
            details.width,
            FileFormat.valueOf(details.fileFormat)
        ),
        Location(
            location.locationLatitude,
            location.locationLongitude
        )
    )

fun ImageScore.toEntity() = ImageScoreEntity(
    id,
    imagePath,
    title,
    score,
    ImageScoreDetailsEntity(
        details.date,
        details.storageSize,
        details.height,
        details.width,
        details.fileFormat.name
    ),
    LocationEntity(
        location.locationLatitude,
        location.locationLongitude
    )
)


