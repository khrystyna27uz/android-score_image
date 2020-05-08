package com.imagescore.domain.mapper

import com.imagescore.data.ui.score.model.ImageScoreDetailsEntity
import com.imagescore.data.ui.score.model.ImageScoreEntity
import com.imagescore.domain.model.FileFormat
import com.imagescore.domain.model.ImageScore
import com.imagescore.domain.model.ImageScoreDetails

fun ImageScoreEntity.toDomain() = ImageScore(
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
    )
)


