package com.imagescore.domain.ui.score.usecase

import com.imagescore.data.ui.score.dao.ImageScoreDao
import com.imagescore.domain.ui.score.mapper.toDomain
import com.imagescore.domain.ui.score.mapper.toEntity
import com.imagescore.domain.ui.score.model.ImageScore
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
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

    fun getById(id: Long): Observable<ImageScore> {
        return dao.findImageScoreById(id).map { item ->
            item.toDomain()
        }
    }

    fun add(imageScore: ImageScore) = Completable.fromAction {
        dao.save(imageScore.toEntity())
    }

    fun update(imageScore: ImageScore) = Completable.fromAction {
        dao.save(imageScore.toEntity())
    }

    fun provideAvailableId() = Single.create<Long> { emitter ->
        val id = dao.getLastId() + 1
        emitter.onSuccess(id)
    }

}