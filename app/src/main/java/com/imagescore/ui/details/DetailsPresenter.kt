package com.imagescore.ui.details

import android.content.pm.PackageManager
import android.location.Location
import com.imagescore.domain.ui.score.usecase.ImageScoreUseCase
import com.imagescore.mvp.BasicPresenter
import com.imagescore.ui.details.view.DetailsView
import com.imagescore.ui.score.model.ImageScoreModel
import com.imagescore.ui.score.model.toDomain
import com.imagescore.ui.score.model.toUI
import com.imagescore.ui.score.view.PERMISSION_ID
import com.imagescore.utils.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class DetailsPresenter(
    private val schedulers: RxSchedulers,
    private val imageScoreUseCase: ImageScoreUseCase
) : BasicPresenter<DetailsView>() {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var imageScore: ImageScoreModel

    lateinit var location: Location

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

    fun onActivityResultReceived(isPermissionLocationGranted: Boolean) {
        permissionsReceived(isPermissionLocationGranted)
    }

    fun permissionsReceived(isPermissionLocationGranted: Boolean) {
        if (isPermissionLocationGranted) {
            getView()?.checkIsLocationEnabled()
        } else {
            getView()?.requestPermissions()
        }
    }

    fun onRequestPermissionsResultReceived(
        requestCode: Int,
        grantResults: IntArray,
        isGranted: Boolean
    ) {
        when (requestCode) {
            PERMISSION_ID -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getView()?.checkIsLocationEnabled()
                }
            }
        }
    }

    fun locationChecked(isEnabled: Boolean) {
        if (isEnabled) {
            getView()?.getLastLocation()
        } else
            getView()?.showOpenLocationSettings()
    }

    fun locationReceived(location: Location?) {
        if (location == null) {
            getView()?.requestNewLocationData()
        } else {
            this.location = location
            if (imageScore.location.locationLatitude <= 0 && imageScore.location.locationLongitude <= 0) {
                compositeDisposable += imageScoreUseCase.update(
                    imageScore.toDomain().copy(
                        location = com.imagescore.domain.ui.score.model.Location(
                            location.latitude,
                            location.longitude
                        )
                    )
                )
                    .subscribeOn(schedulers.io())
                    .observeOn(schedulers.mainThread())
                    .subscribe()
            }
        }
    }
}