package com.imagescore.ui.score.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.imagescore.R
import com.imagescore.ui.details.view.DetailsFragment
import com.imagescore.ui.score.ScorePresenter
import com.imagescore.ui.score.adapter.ScoreAdapter
import com.imagescore.ui.score.model.ImageScoreDetails
import com.imagescore.ui.score.model.ImageScoreModel
import com.imagescore.utils.file.FileUtil
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_score.*
import java.util.*
import javax.inject.Inject


const val IMAGE_SCORE_SPAN_COUNT = 2
const val CAMERA_REQUEST = 1888
const val CAMERA_PERMISSION_CODE = 200
const val LOCATION_PERMISSON_CODE = 300
const val BUNDLE_IMAGE_ID = "image_id"
const val DEFAULT_SCORE = 0
const val DEFAULT_ID = 0L
const val PERMISSION_ID = 42

class ScoreFragment : Fragment(R.layout.fragment_score), ScoreView, ScoreAdapter.ScoreCallback {

    @Inject
    lateinit var presenter: ScorePresenter

    private lateinit var fragment: DetailsFragment

    private inline val adapter get() = scoreRV.adapter as? ScoreAdapter

    lateinit var locationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AndroidSupportInjection.inject(this)
        presenter.enterWithView(this)
        setUpUI()
        locationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        getLastLocation()
    }

    @SuppressLint("MissingPermission")
    override fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                locationClient.lastLocation.addOnCompleteListener(activity!!) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        location
                    }
                }
            } else {
                Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        locationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        locationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
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
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
        presenter.onRequestPermissionsResultReceived(
            requestCode,
            grantResults,
            grantResults[0] != PackageManager.PERMISSION_GRANTED
        )
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

        val permissionLocation = activity?.let {
            ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
        }
        //  presenter.onPermissionLocationStatusReceived(permissionLocation == PackageManager.PERMISSION_GRANTED)

    }

}