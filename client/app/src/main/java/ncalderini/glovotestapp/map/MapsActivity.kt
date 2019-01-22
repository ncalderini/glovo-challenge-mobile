package ncalderini.glovotestapp.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolygonOptions
import kotlinx.android.synthetic.main.activity_maps.*
import ncalderini.glovotestapp.R
import ncalderini.glovotestapp.countryselect.CountrySelectActivity
import ncalderini.glovotestapp.model.City
import ncalderini.glovotestapp.model.CityMarker
import ncalderini.glovotestapp.model.Country
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
    GoogleMap.OnMarkerClickListener, EasyPermissions.PermissionCallbacks {

    companion object {
        private const val TAG = "MapsActivity"
        private const val REQUEST_CODE_LOCATION = 123
        private const val REQUEST_CODE_CITY_PICKER = 10
        private const val MAX_ZOOM_LEVEL = 10f
    }

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var model: MapViewModel

    private val pinnedMarkers: MutableList<Marker> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        model = ViewModelProviders.of(this).get(MapViewModel::class.java)
        model.getCountries().observe(this, Observer<List<Country>>{ countries ->
            countries.forEach { country ->
                country.cities.forEach { city -> drawCityWorkingAreaBoundsOnMap(city) }
            }
            enableMyLocation()
            addMarkersToMap()
        })
    }

    /**
     * Add city markers to google map.
     * Markers are stored in a list for further manipulation
     */
    private fun addMarkersToMap() {
        model.getCityMarkers().observe(this, Observer<List<CityMarker>> { cityMarkerList ->
            cityMarkerList.forEach { cityMarker ->
                val mapMarker = mMap.addMarker(cityMarker.marker)
                mapMarker.tag = cityMarker.city
                pinnedMarkers.add(mapMarker)
            }
        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setOnCameraMoveListener(this)
        mMap.setOnMarkerClickListener(this)
    }

    /**
     * enableMyLocation() will enable the location of the map if the user has given permission
     * for the app to access their device location.
     * Android Studio requires an explicit check before setting map.isMyLocationEnabled to true
     * but we are using EasyPermissions to handle it so we can suppress the "MissingPermission"
     * check.
     */
    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_CODE_LOCATION)
    private fun enableMyLocation() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                val latLngLocation = LatLng(location.latitude, location.longitude)
                if (model.isInsideWorkingArea(latLngLocation)) {
                    centerMapOnUserLocation(latLngLocation)
                } else {
                    val intent = CountrySelectActivity.getActivityIntent(this, model.getCountries().value!!)
                    startActivityForResult(intent, REQUEST_CODE_CITY_PICKER)
                }
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.location_rationale),
                REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size)

        val intent = CountrySelectActivity.getActivityIntent(this, model.getCountries().value!!)
        startActivityForResult(intent, REQUEST_CODE_CITY_PICKER)
    }

    override fun onCameraMove() {
        val cameraPosition = mMap.cameraPosition
        showCityMarkers(cameraPosition.zoom < MAX_ZOOM_LEVEL)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        val city = marker?.tag as City
        centerMapOnCity(city)
        return true
    }

    /**
     * Centers google map view on the city working area bounds.
     * @param city City to be centered
     */
    private fun centerMapOnCity(city: City) {
        val position = city.workingAreaBounds?.areaBounds!!
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(position, 10))
        progress_bar.visibility = View.VISIBLE
        model.getCityDetailsFromPosition(position.center).observe(this, Observer<City> { cityDetails ->
            city_name.text = cityDetails.name
            city_currency.text = cityDetails.currency
            city_time_zone.text = cityDetails.time_zone
            progress_bar.visibility = View.GONE
        })
    }

    /**
     * Centers google map view on the user's current location.
     * @param userLocation User's current location
     */
    private fun centerMapOnUserLocation(userLocation: LatLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15.0f))
        progress_bar.visibility = View.VISIBLE
        model.getCityDetailsFromPosition(userLocation).observe(this, Observer<City> { cityDetails ->
            city_name.text = cityDetails.name
            city_currency.text = cityDetails.currency
            city_time_zone.text = cityDetails.time_zone
            progress_bar.visibility = View.GONE
        })
    }

    /**
     * Show or Hide City Markers on map.
     * @param show `true` to show city markers, `false` otherwise.
     */
    private fun showCityMarkers(show: Boolean) {
       pinnedMarkers.forEach { marker -> marker.isVisible = show }
    }

    /**
     * Draws the City's Working Area on map
     */
    private fun drawCityWorkingAreaBoundsOnMap(city: City) {
        city.workingAreaBounds?.workingAreaLatLngList?.forEach { latLngList ->
            mMap.addPolygon(PolygonOptions()
                .addAll(latLngList)
                .strokeWidth(0f)
                .fillColor(ContextCompat.getColor(this, R.color.colorPrimaryTransparent))
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CITY_PICKER) {
            val selectedCity: City = data?.getParcelableExtra(CountrySelectActivity.ARG_SELECTED_CITY) as City
            centerMapOnCity(selectedCity)
        }
    }
}
