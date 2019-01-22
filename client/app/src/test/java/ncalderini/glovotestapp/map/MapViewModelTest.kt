package ncalderini.glovotestapp.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ncalderini.glovotestapp.model.City
import ncalderini.glovotestapp.model.Country
import ncalderini.glovotestapp.networking.GlovoApi
import ncalderini.glovotestapp.util.TestUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import retrofit2.Response

class MapViewModelTest {

    @get:Rule var rule: TestRule = InstantTaskExecutorRule()

    private val mockApi: GlovoApi = mock()
    private val model = MapViewModel()

    @Before
    fun setUp() {
        val glovoApiField = model::class.java.getDeclaredField("glovoService")
        glovoApiField.isAccessible = true
        glovoApiField.set(model, mockApi)

        val dispatcherField = model::class.java.getDeclaredField("dispatcher")
        dispatcherField.isAccessible = true
        dispatcherField.set(model, Dispatchers.Unconfined)

        fetchMockData()
    }

    @Test fun test_getCountries_returnsCountryList_withMergedCityData() = runBlocking {
        val countries = model.getCountries().value!!
        assertFalse(countries.isEmpty())

        countries.forEach { country ->
            country.cities.forEach { city ->
                assertEquals(country.code, city.country_code)
            }
        }
    }

    @Test
    fun test_getCityFromPosition_returnsCorrectCity() = runBlocking {
        val countries = model.getCountries().value!!
        val expectedCity = countries[0].cities[0]
        val position = expectedCity.workingAreaBounds?.areaBounds?.center!!

        assertEquals(expectedCity, model.getCityDetailsFromPosition(position).value!!)
    }

    @Test
    fun test_getCityMarkers_returnsCorrectMarker() = runBlocking {
        val countries = model.getCountries().value!!
        val markers = model.getCityMarkers().value!!
        val expectedCity = countries[0].cities[0]
        val expectedPosition = expectedCity.workingAreaBounds?.areaBounds?.center!!

        assertEquals(expectedCity, markers[0].city)
        assertEquals("Buenos Aires", markers[0].marker.title)
        assertEquals(expectedPosition, markers[0].marker.position)
    }

    private fun fetchMockData() {
        val countryResponse: Response<List<Country>> = Response.success(TestUtils.getMockCountryListData())
        val deferredCountryResponse = CompletableDeferred(countryResponse)

        val cityResponse: Response<List<City>> = Response.success(TestUtils.getMockCityListData())
        val deferredCityResponse = CompletableDeferred(cityResponse)

        val cityDetailsResponse = Response.success(TestUtils.getMockCityDetailsData())
        val deferredCityDetailResponse = CompletableDeferred(cityDetailsResponse)

        whenever(mockApi.getCountries()).thenReturn(deferredCountryResponse)
        whenever(mockApi.getCities()).thenReturn(deferredCityResponse)
        whenever(mockApi.getCityDetails(any())).thenReturn(deferredCityDetailResponse)
    }
}