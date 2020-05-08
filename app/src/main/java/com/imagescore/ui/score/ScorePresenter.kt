package com.imagescore.ui.score

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import com.imagescore.domain.usecase.ImageScoreUseCase
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

    fun onActivityResultReceived(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.let {
                val photo = it.extras.get(PHOTO_DATA) as Bitmap
                getView()?.setUpPhoto(photo)
                return
            } ?: run {
                getView()?.setUpPhotoError()
            }
        }

        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            data?.let {
                val photo = it.extras.get(PHOTO_DATA) as Bitmap
                getView()?.setUpPhoto(photo)
                return
            } ?: run {
                getView()?.setUpPhotoError()
            }
        }
    }

    fun onRequestPermissionsResultReceived(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    getView()?.onPermissionDenied()
                } else {
                    getView()?.makeRequestCamera()
                }
            }

            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
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