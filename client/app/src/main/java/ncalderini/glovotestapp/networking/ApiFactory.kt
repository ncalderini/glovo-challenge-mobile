package ncalderini.glovotestapp.networking

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import ncalderini.glovotestapp.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiFactory {
    //OkhttpClient for building http request url
    private val glovoClient = OkHttpClient().newBuilder()
        .build()

    private fun retrofit() : Retrofit = Retrofit.Builder()
        .client(glovoClient)
        .baseUrl(BuildConfig.API_SERVICE)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()


    val glovoApi : GlovoApi = retrofit().create(GlovoApi::class.java)
}