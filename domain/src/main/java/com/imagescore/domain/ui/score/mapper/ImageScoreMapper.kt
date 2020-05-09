package com.imagescore.domain.ui.score.mapper

import com.imagescore.data.ui.score.model.ImageScoreDetailsEntity
import com.imagescore.data.ui.score.model.ImageScoreEntity
import com.imagescore.domain.ui.score.model.FileFormat
import com.imagescore.domain.ui.score.model.ImageScore
import com.imagescore.domain.ui.score.model.ImageScoreDetails

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


