package com.imagescore.ui.score.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.imagescore.R
import com.imagescore.ui.details.view.DetailsFragment
import com.imagescore.ui.score.ScorePresenter
import com.imagescore.ui.score.adapter.ScoreAdapter
import com.imagescore.ui.score.model.ImageScoreDetails
import com.imagescore.ui.score.model.ImageScoreModel
import com.imagescore.utils.file.FileUtil
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_score.*
import javax.inject.Inject

const val IMAGE_SCORE_SPAN_COUNT = 2
const val CAMERA_REQUEST = 1888
const val CAMERA_PERMISSION_CODE = 200
const val BUNDLE_IMAGE_ID = "image_id"
const val DEFAULT_SCORE = 0
const val DEFAULT_ID = 0L

class ScoreFragment : Fragment(R.layout.fragment_score), ScoreView, ScoreAdapter.ScoreCallback {

    @Inject
    lateinit var presenter: ScorePresenter

    private lateinit var fragment: DetailsFragment

    private inline val adapter get() = scoreRV.adapter as? ScoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AndroidSupportInjection.inject(this)
        presenter.enterWithView(this)
        setUpUI()
    }

    override fun onDestroy() {
        presenter.exitFromView()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        presenter.onRequestPermissionsResultReceived(
            requestCode,
            grantResults,
            grantResults[0] != PackageManager.PERMISSION_GRANTED
        )
    }

    override fun onPermissionDenied() {
        Toast.makeText(activity, getString(R.string.denied), Toast.LENGTH_SHORT).show()
    }

    override fun setScoreData(data: List<ImageScoreModel>) {
        adapter?.submitList(data)
    }

    override fun details(imageScoreModel: ImageScoreModel) {
        fragment = DetailsFragment()
        val bundle = Bundle()
        bundle.putLong(BUNDLE_IMAGE_ID, imageScoreModel.id)
        fragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.mainContainer, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun score(imageScoreModel: ImageScoreModel, score: Int) {
        presenter.onScoreReceived(imageScoreModel, score)
    }

    override fun openCamera(photoUri: Uri?) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        startActivityForResult(intent, CAMERA_REQUEST)
    }

    override fun makeRequestCamera() {
        activity?.let {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            presenter.onActivityResultReceived()
        }

    }

    override fun onPhotoTaken(imageUri: Uri?) {
        val uri = imageUri ?: return
        val photoSize = FileUtil.getSize(context!!, uri)
        val photoWidth = FileUtil.getImageWidth(uri, context!!)
        val photoHeight = FileUtil.getImageHeight(uri, context!!)
        val photoDate = FileUtil.getImageDate(uri, context!!)
        val photoFormat = FileUtil.getImageFormat(uri)
        val photoName = context?.let { FileUtil.getFileName(uri, it) }
        val model = ImageScoreModel(
            DEFAULT_ID,
            uri.toString(),
            photoName!!,
            DEFAULT_SCORE,
            ImageScoreDetails(photoDate, photoSize, photoHeight, photoWidth, photoFormat)
        )
        presenter.onPhotoReceived(model)

    }

    override fun setUpPhotoError() =
        Toast.makeText(activity, getString(R.string.error_photo_load), Toast.LENGTH_SHORT).show()

    override fun setUpUI() {
        val gridLayoutManager = GridLayoutManager(context, IMAGE_SCORE_SPAN_COUNT)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        scoreRV.layoutManager = gridLayoutManager
        scoreRV.adapter = ScoreAdapter(this, requireContext())
        addPhotoFB.setOnClickListener {
            val permissionCamera = activity?.let {
                ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA)
            }
            presenter.onAddPhotoClicked(
                permissionCamera == PackageManager.PERMISSION_GRANTED,
                FileUtil.generatePhotoUri(context!!)
            )
        }
    }
}