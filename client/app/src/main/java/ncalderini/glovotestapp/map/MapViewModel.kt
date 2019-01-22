package ncalderini.glovotestapp.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ncalderini.glovotestapp.model.City
import ncalderini.glovotestapp.model.CityMarker
import ncalderini.glovotestapp.model.Country
import ncalderini.glovotestapp.model.WorkingArea
import ncalderini.glovotestapp.networking.ApiFactory

class MapViewModel : ViewModel() {

    private val glovoService = ApiFactory.glovoApi

    private lateinit var countries: MutableLiveData<List<Country>>
    private lateinit var cityMarkers: MutableLiveData<List<CityMarker>>
    private lateinit var cityDetails: MutableLiveData<City>
    private lateinit var cities: List<City>

    fun getCountries() : LiveData<List<Country>> {
        if (!::countries.isInitialized) {
            countries = MutableLiveData()
            fetchCountries()
        }
        return countries
    }

    private fun fetchCountries() {
        GlobalScope.launch(Dispatchers.Main) {
            val countryRequest = glovoService.getCountries()
            val citiesRequest = glovoService.getCities()
            try {
                val countryResponse = countryRequest.await()
                val citiesResponse = citiesRequest.await()
                if (countryResponse.isSuccessful && citiesResponse.isSuccessful) {
                    cities = citiesResponse.body()!!
                    decodePolylines()
                    countries.value = mergeResults(countryResponse.body(), cities)
                }
            } catch (exception : Exception) {

            }
        }
    }

    /**
     * @return The city that encloses this position
     */
    fun getCityDetailsFromPosition(position: LatLng) : LiveData<City> {
        cityDetails = MutableLiveData()
        val currentCity = getCurrentCityFromPosition(position)
        currentCity?.let { fetchCityDetails(currentCity.code) }
        return cityDetails
    }

    private fun fetchCityDetails(cityCode: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val cityDetailRequest = glovoService.getCityDetails(cityCode)
            try {
                val cityDetailsResponse = cityDetailRequest.await()
                if (cityDetailsResponse.isSuccessful) {
                    cityDetails.value = cityDetailsResponse.body()
                }
            } catch (exception : Exception) {

            }
        }
    }

    /**
     * @return A list of markers with the position of each city
     */
    fun getCityMarkers() : LiveData<List<CityMarker>> {
        if (!::cityMarkers.isInitialized) {
            cityMarkers = MutableLiveData()
            val markersList: MutableList<CityMarker> = ArrayList()
                cities.forEach { city ->
                    val marker = MarkerOptions()
                        .position(city.workingAreaBounds?.areaBounds?.center!!)
                        .title(city.name)

                    markersList.add(CityMarker(city, marker))
                }
            cityMarkers.value = markersList
        }
        return cityMarkers
    }

    /**
     * Appends City data to each country
     */
    private fun mergeResults(countries: List<Country>?, cities: List<City>?) : List<Country> {
        val fullCountryList = ArrayList<Country>()
        countries?.forEach { country ->
            val citiesFromCountry = cities?.filter { city -> country.code == city.country_code }
            citiesFromCountry?.let {
                country.cities = citiesFromCountry
                fullCountryList.add(country)
            }
        }

        return fullCountryList
    }

    private fun decodePolylines() {
        cities.forEach { city ->
            val workingAreaLatLngList = ArrayList<List<LatLng>>()
            city.working_area.forEach { workingArea ->
                if (!workingArea.isEmpty()) {
                    val latLngList = PolyUtil.decode(workingArea)
                    workingAreaLatLngList.add(latLngList)
                }
            }
            city.workingAreaBounds = WorkingArea(workingAreaLatLngList)
        }
    }

    /**
     * @param position Position to be queried.
     * @return `true` if `position` is inside of a Working Area. `false` otherwise
     */
    fun isInsideWorkingArea(position: LatLng) : Boolean {
        cities.forEach { city ->
            city.workingAreaBounds?.let {
                if (it.positionIsInsideWorkingArea(position)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * @param position Position to be queried
     * @return `city` if this position is inside a City's Working area bounds. `null` otherwise
     */
    private fun getCurrentCityFromPosition(position: LatLng) : City? {
        cities.forEach { city ->
            city.workingAreaBounds?.let {
                if (it.positionIsInsideWorkingArea(position)) {
                    return city
                }
            }
        }
        return null
    }
}