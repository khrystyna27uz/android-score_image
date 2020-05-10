package com.imagescore.ui.details.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.imagescore.R
import com.imagescore.ui.details.DetailsPresenter
import com.imagescore.ui.main.view.MainActivity
import com.imagescore.ui.score.model.ImageScoreModel
import com.imagescore.ui.score.view.BUNDLE_IMAGE_ID
import com.imagescore.ui.score.view.PERMISSION_ID
import com.imagescore.utils.file.LocationUtil
import com.imagescore.utils.toDateString
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_detail.*
import javax.inject.Inject

const val SHARE_TYPE = "image/jpg"

class DetailsFragment : Fragment(R.layout.fragment_detail), DetailsView,
    EditTitleDialog.EditDialogInteraction {

    @Inject
    lateinit var presenter: DetailsPresenter

    lateinit var locationClient: FusedLocationProviderClient

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            presenter.locationReceived(locationResult.lastLocation)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AndroidSupportInjection.inject(this)
        presenter.enterWithView(this)
        locationClient = LocationServices.getFusedLocationProviderClient(activity!!)
    }

    override fun onDestroy() {
        presenter.exitFromView()
        super.onDestroy()
    }

    // Set up back button for top toolbar
    // Receive image id from previous Fragment and pass in to current presenter
    override fun setUpUI() {
        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        editIV.setOnClickListener { presenter.onEditTitleClicked(titleTV.text.toString()) }
        shareFB.setOnClickListener { presenter.onSharePhotoClicked() }
        presenter.imageIdReceived(arguments!!.getLong(BUNDLE_IMAGE_ID))
    }

    // Received image data from database and setup UI
    override fun setScoreData(imageScore: ImageScoreModel) {
        titleTV.text = imageScore.title
        if (imageScore.location.locationLatitude > 0) {
            var location = LocationUtil.getAddressFromCoordinates(context!!, imageScore.location)
            locationTV.text = location
            dateTV.text =
                String.format(
                    getString(R.string.imageDate),
                    imageScore.details.date.toDateString()

                )
        }
        storageTV.text =
            String.format(
                getString(R.string.imageStorageSize),
                imageScore.details.storageSize
            )

        heightTV.text =
            String.format(
                getString(R.string.imageHeight),
                imageScore.details.height
            )

        widthTV.text =
            String.format(
                getString(R.string.imageWidth),
                imageScore.details.width
            )
        fileFormatTV.text =
            String.format(
                getString(R.string.imageFileFormat),
                imageScore.details.fileFormat

            )
        ratingBar.rating = imageScore.score.toFloat()
        Glide.with(this)
            .load(imageScore.imagePath)
            .placeholder(R.drawable.no_photo)
            .into(pictureIV)
        // подумати куди поставити цей метод
        presenter.permissionsReceived(checkPermissions())
    }

    override fun goToEditTitleDialog(currentTitle: String) {
        EditTitleDialog.show(this, currentTitle)
    }

    override fun applyEdit(updatedTitle: String) {
        presenter.onTitleEdited(updatedTitle)
    }

    // Share photo with possibility to way how to share
    override fun sharePhoto(imagePath: String) {
        val intent = ShareCompat.IntentBuilder.from(activity as MainActivity)
            .setType(SHARE_TYPE)
            .setStream(Uri.parse(imagePath))
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    override fun getLastLocation() {
        locationClient.lastLocation.addOnCompleteListener(activity!!) { task ->
            val location: Location? = task.result
            presenter.locationReceived(location)
        }
    }

    override fun checkIsLocationEnabled() {
        presenter.locationChecked(LocationUtil.isLocationEnabled(context!!))
    }

    @SuppressLint("MissingPermission")
    override fun requestNewLocationData() {
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

    override fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun showOpenLocationSettings() {
        Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResultReceived(checkPermissions())

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
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

}