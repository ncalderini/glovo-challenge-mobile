package ncalderini.glovotestapp.networking

import kotlinx.coroutines.Deferred
import ncalderini.glovotestapp.model.City
import ncalderini.glovotestapp.model.Country
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

//A retrofit Network Interface for the Api
interface GlovoApi {

    @GET("countries")
    fun getCountries(): Deferred<Response<List<Country>>>

    @GET("cities")
    fun getCities(): Deferred<Response<List<City>>>

    @GET("cities/{code}")
    fun getCityDetails(@Path("code") code: String): Deferred<Response<City>>
}