package com.imagescore.ui.score.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.imagescore.R
import com.imagescore.domain.ui.score.model.FileFormat
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
const val STRAIGHT_CORNER = 90F
const val BUNDLE_IMAGE_ID = "image_id"

class ScoreFragment : Fragment(R.layout.fragment_score), ScoreView, ScoreAdapter.ScoreCallback {

    @Inject
    lateinit var presenter: ScorePresenter

    lateinit var fragment: DetailsFragment

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResultReceived(requestCode, resultCode, data?.extras)
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

    override fun makeRequestWritePermissions(requestCode: Int) {
        activity?.let {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                requestCode
            )
        }
    }

    override fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    override fun makeRequestCamera() {
        activity?.let {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    override fun setUpPhoto(photo: Bitmap) {
        var photoPath: Uri? = null
        if (photo.width > photo.height) {
            val matrix = Matrix()
            matrix.postRotate(STRAIGHT_CORNER)
            val scaledBitmap = Bitmap.createScaledBitmap(photo, photo.width, photo.height, true)
            val rotatedBitmap = Bitmap.createBitmap(
                scaledBitmap,
                0,
                0,
                scaledBitmap.width,
                scaledBitmap.height,
                matrix,
                true
            )
            photoPath = context?.let { FileUtil.getImageUri(it, rotatedBitmap) }
        } else {
            photoPath = context?.let { FileUtil.getImageUri(it, photo) }
        }
        val photoName = context?.let { FileUtil.getFileName(photoPath, it) }
        val model = ImageScoreModel(
            0L,
            photoPath.toString(),
            photoName!!,
            0,
            ImageScoreDetails(0, 0, 0, 0, FileFormat.JPEG)
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
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                )
            }
            val permission = activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            presenter.onAddPhotoClicked(
                permission == PackageManager.PERMISSION_GRANTED,
                permissionCamera == PackageManager.PERMISSION_GRANTED
            )
        }
    }
}