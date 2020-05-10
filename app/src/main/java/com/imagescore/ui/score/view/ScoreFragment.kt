package com.imagescore.ui.score.view

import android.Manifest
import android.app.Activity
import android.content.Context
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
import com.imagescore.ui.main.view.Navigation
import com.imagescore.ui.score.ScorePresenter
import com.imagescore.ui.score.adapter.ScoreAdapter
import com.imagescore.ui.score.model.ImageScoreModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_score.*
import javax.inject.Inject

private const val IMAGE_SCORE_SPAN_COUNT = 2
private const val CAMERA_REQUEST = 1888
private const val CAMERA_PERMISSION_CODE = 200

class ScoreFragment : Fragment(R.layout.fragment_score), ScoreView, ScoreAdapter.ScoreCallback {

    companion object {
        fun newInstance() = ScoreFragment()
    }

    @Inject
    lateinit var presenter: ScorePresenter

    private lateinit var navigation: Navigation

    private inline val adapter get() = scoreRV.adapter as? ScoreAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AndroidSupportInjection.inject(this)
        presenter.enterWithView(this)
        setUpUI()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        navigation = if (context is Navigation) {
            context
        } else {
            throw IllegalStateException("$context must implement Navigation")
        }
    }

    override fun onDestroy() {
        presenter.exitFromView()
        super.onDestroy()
    }

    override fun onPermissionDenied() {
        Toast.makeText(activity, getString(R.string.denied), Toast.LENGTH_SHORT).show()
    }

    override fun setScoreData(data: List<ImageScoreModel>) {
        adapter?.submitList(data)
    }

    override fun details(imageScoreModel: ImageScoreModel) {
        navigation.navigate(DetailsFragment.newInstance(imageScoreModel.id))
    }

    override fun score(imageScoreModel: ImageScoreModel, score: Int) {
        presenter.onScoreReceived(imageScoreModel, score)
    }

    override fun openCamera(photoUri: Uri) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        startActivityForResult(intent, CAMERA_REQUEST)
    }

    override fun makeRequestCamera() {
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            presenter.onActivityResultReceived()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            presenter.onRequestPermissionsResultReceived(
                isGranted = hasCameraPermission()
            )
        }
    }

    override fun setUpPhotoError() =
        Toast.makeText(activity, getString(R.string.error_photo_load), Toast.LENGTH_SHORT).show()

    override fun setUpUI() {
        val gridLayoutManager = GridLayoutManager(context, IMAGE_SCORE_SPAN_COUNT)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        scoreRV.layoutManager = gridLayoutManager
        scoreRV.adapter = ScoreAdapter(this, requireContext())
        addPhotoFB.setOnClickListener {
            presenter.onAddPhotoClicked(
                isAllowedCamera = hasCameraPermission()
            )
        }
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}