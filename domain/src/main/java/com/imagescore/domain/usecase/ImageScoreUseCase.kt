package com.imagescore.domain.usecase

import com.imagescore.data.ui.score.dao.ImageScoreDao
import com.imagescore.domain.mapper.toDomain
import com.imagescore.domain.mapper.toEntity
import com.imagescore.domain.model.ImageScore
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class ImageScoreUseCase @Inject constructor(
    private val dao: ImageScoreDao
) {
    fun get(): Observable<List<ImageScore>> {
        return dao.getAll().map { list ->
            list.map { item ->
                item.toDomain()
            }
        }
    }

    fun add(imageScore: ImageScore) = Completable.fromAction {
        dao.save(imageScore.toEntity())
    }

    fun update(imageScore: ImageScore) = Completable.fromAction {
        dao.save(imageScore.toEntity())
    }

}