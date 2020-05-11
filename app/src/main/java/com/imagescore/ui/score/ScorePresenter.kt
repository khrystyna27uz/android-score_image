package com.imagescore.ui.score

import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import com.imagescore.domain.ui.score.usecase.ImageScoreUseCase
import com.imagescore.mvp.BasicPresenter
import com.imagescore.ui.score.model.ImageScoreModel
import com.imagescore.ui.score.model.toDomain
import com.imagescore.ui.score.model.toUi
import com.imagescore.ui.score.view.CAMERA_PERMISSION_CODE
import com.imagescore.ui.score.view.ScoreView
import com.imagescore.utils.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

const val REQUEST_CAMERA = 300

class ScorePresenter(
    private val schedulers: RxSchedulers,
    private val imageScoreUseCase: ImageScoreUseCase
) : BasicPresenter<ScoreView>() {

    private val compositeDisposable = CompositeDisposable()

    private var uri: Uri? = null

    override fun onEnterScope() {
        super.onEnterScope()
        getView()?.setUpUI()
        compositeDisposable += imageScoreUseCase
            .get()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe({
                getView()?.setScoreData(it.toUi())
            }, {

            })
    }

    override fun onExitScope() {
        super.onExitScope()
        compositeDisposable.clear()
    }

    fun onRequestPermissionsResultReceived(
        requestCode: Int,
        grantResults: IntArray,
        isGranted: Boolean
    ) {
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (grantResults.isEmpty() || isGranted) {
                    getView()?.onPermissionDenied()
                } else {
                    getView()?.makeRequestCamera()
                }
            }

            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isEmpty() || isGranted) {
                    getView()?.onPermissionDenied()
                } else {
                    getView()?.openCamera(uri)
                }
            }
        }
    }

    fun onAddPhotoClicked(isAllowedCamera: Boolean, uri: Uri) {
        this.uri = uri
        if (isAllowedCamera) {
            getView()?.openCamera(uri)
        } else {
            getView()?.makeRequestCamera()

        }
    }

    fun onPhotoReceived(imageScoreModel: ImageScoreModel) {
        compositeDisposable += imageScoreUseCase.add(imageScoreModel.toDomain())
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe()
    }

    fun onScoreReceived(imageScoreModel: ImageScoreModel, score: Int) {
        compositeDisposable += imageScoreUseCase.update(
            imageScoreModel.toDomain().copy(score = score)
        )
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe()
    }

    fun onActivityResultReceived() {
        getView()?.onPhotoTaken(uri)
    }

}