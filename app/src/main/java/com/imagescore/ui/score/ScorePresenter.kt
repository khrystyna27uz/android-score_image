package com.imagescore.ui.score

import android.content.Context
import com.imagescore.domain.ui.score.usecase.ImageScoreUseCase
import com.imagescore.mvp.BasicPresenter
import com.imagescore.ui.score.model.*
import com.imagescore.ui.score.view.ScoreView
import com.imagescore.utils.file.FileUtil
import com.imagescore.utils.file.PhotoUri
import com.imagescore.utils.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.*

class ScorePresenter(
    private val context: Context,
    private val schedulers: RxSchedulers,
    private val imageScoreUseCase: ImageScoreUseCase
) : BasicPresenter<ScoreView>() {

    private val compositeDisposable = CompositeDisposable()

    private var photoUri: PhotoUri? = null

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

    fun onRequestPermissionsResultReceived(isGranted: Boolean) {
        if (isGranted) {
            getView()?.openCamera(getOrGenerateUri())
        } else {
            getView()?.onPermissionDenied()
        }
    }

    fun onAddPhotoClicked(isAllowedCamera: Boolean) {
        if (isAllowedCamera) {
            getView()?.openCamera(getOrGenerateUri())
        } else {
            getView()?.makeRequestCamera()
        }
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
        photoUri?.let(::onPhotoTaken)
        photoUri = null
    }

    private fun onPhotoTaken(uri: PhotoUri) = getAvailableId { id ->
        val photoSize = FileUtil.getImageSize(context, uri.uri)
        val photoName = "Photo #$id"
        val model = ImageScoreModel(
            id = id,
            imagePath = uri.uri.toString(),
            title = photoName,
            score = 0,
            details = ImageScoreDetails(
                date = Date().time,
                storageSize = FileUtil.getFileSize(context, uri.uri),
                width = photoSize.width.toLong(),
                height = photoSize.height.toLong(),
                fileFormat = uri.format
            ),
            location = Location(0.0,0.0)
        )

        compositeDisposable += imageScoreUseCase.add(model.toDomain())
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe()
    }

    private inline fun getAvailableId(crossinline block: (id: Long) -> Unit) {
        compositeDisposable += imageScoreUseCase.provideAvailableId()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe({ id ->
                block(id)
            }, {
                it.printStackTrace()
            })
    }

    private fun getOrGenerateUri() = photoUri?.let {
        return it.uri
    } ?: run {
        photoUri = FileUtil.generatePhotoUri(context)
        photoUri!!.uri
    }
}