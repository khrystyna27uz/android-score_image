package com.imagescore.ui.score

import android.graphics.Bitmap
import android.os.Bundle
import com.imagescore.domain.ui.score.usecase.ImageScoreUseCase
import com.imagescore.mvp.BasicPresenter
import com.imagescore.ui.score.model.ImageScoreModel
import com.imagescore.ui.score.model.toDomain
import com.imagescore.ui.score.model.toUi
import com.imagescore.ui.score.view.CAMERA_PERMISSION_CODE
import com.imagescore.ui.score.view.CAMERA_REQUEST
import com.imagescore.ui.score.view.ScoreView
import com.imagescore.utils.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

const val REQUEST_CAMERA = 300
const val PHOTO_DATA = "data"
const val RESULT_OK = -1

class ScorePresenter(
    private val schedulers: RxSchedulers,
    private val imageScoreUseCase: ImageScoreUseCase
) : BasicPresenter<ScoreView>() {

    private val compositeDisposable = CompositeDisposable()

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

    fun onActivityResultReceived(requestCode: Int, resultCode: Int, data: Bundle?) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            data?.let {
                val photo = it.get(PHOTO_DATA) as Bitmap
                getView()?.setUpPhoto(photo)
                return
            } ?: run {
                getView()?.setUpPhotoError()
            }
        }

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            data?.let {
                val photo = it.get(PHOTO_DATA) as Bitmap
                getView()?.setUpPhoto(photo)
                return
            } ?: run {
                getView()?.setUpPhotoError()
            }
        }
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
                    getView()?.openCamera()
                }
            }
        }
    }

    fun onAddPhotoClicked(isAllowedWrite: Boolean, isAllowedCamera: Boolean) {
        if (!isAllowedWrite) {
            getView()?.makeRequestWritePermissions(REQUEST_CAMERA)
        } else if (isAllowedCamera) {
            getView()?.openCamera()
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

}