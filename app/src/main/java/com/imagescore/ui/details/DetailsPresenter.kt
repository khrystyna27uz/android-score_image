package com.imagescore.ui.details

import com.imagescore.domain.ui.score.usecase.ImageScoreUseCase
import com.imagescore.mvp.BasicPresenter
import com.imagescore.ui.details.view.DetailsView
import com.imagescore.ui.score.model.ImageScoreModel
import com.imagescore.ui.score.model.toDomain
import com.imagescore.ui.score.model.toUI
import com.imagescore.utils.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class DetailsPresenter(
    private val schedulers: RxSchedulers,
    private val imageScoreUseCase: ImageScoreUseCase
) : BasicPresenter<DetailsView>() {

    private val compositeDisposable = CompositeDisposable()

    lateinit var imageScore: ImageScoreModel

    override fun onEnterScope() {
        super.onEnterScope()
        getView()?.setUpUI()
    }

    override fun onExitScope() {
        super.onExitScope()
        compositeDisposable.clear()
    }

    fun imageIdReceived(id: Long) {
        compositeDisposable += imageScoreUseCase
            .getById(id)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe({
                imageScore = it.toUI()
                getView()?.setScoreData(imageScore)
            }, {

            })
    }

    fun onEditTitleClicked(currentTitle: String) {
        getView()?.goToEditTitleDialog(currentTitle)
    }

    fun onTitleEdited(updatedTitle: String) {
        compositeDisposable += imageScoreUseCase.update(
            imageScore.toDomain().copy(title = updatedTitle)
        )
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe()
    }

    fun onSharePhotoClicked() {
        getView()?.sharePhoto(imageScore.imagePath)
    }
}