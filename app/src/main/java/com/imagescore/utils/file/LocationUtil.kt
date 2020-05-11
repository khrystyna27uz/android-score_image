package com.imagescore.utils.file

import android.content.Context
import android.location.Address
import android.location.Geocoder
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
            )
            val city: String = addresses[0].locality
            val country: String = addresses[0].countryName
            val postalCode: String = addresses[0].postalCode
            return "$city ,$country ,$postalCode"
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