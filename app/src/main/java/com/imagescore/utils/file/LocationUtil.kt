package com.imagescore.utils.file

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import java.util.*

class LocationUtil {
    companion object {

        fun getAddressFromCoordinates(
            context: Context,
            location: com.imagescore.ui.score.model.Location
        ): String {
            val addresses: List<Address>
            val geocoder = Geocoder(context, Locale.getDefault())
            addresses = geocoder.getFromLocation(
                location.locationLatitude,
                location.locationLongitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            val address: String = addresses[0]
                .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            val city: String = addresses[0].getLocality()
            val state: String = addresses[0].getAdminArea()
            val country: String = addresses[0].getCountryName()
            val postalCode: String = addresses[0].getPostalCode()
            val knownName: String = addresses[0].getFeatureName()
            return city + " ," + state + " ," + country + " ," + postalCode
        }

        fun isLocationEnabled(context: Context): Boolean {
            val locationManager: LocationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }
    }
}