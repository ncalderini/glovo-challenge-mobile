package ncalderini.glovotestapp.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class WorkingArea(val workingAreaLatLngList: List<List<LatLng>>) : Parcelable {

    @IgnoredOnParcel
    val areaBounds: LatLngBounds

    init {
        val latLngBuilder = LatLngBounds.builder()
        workingAreaLatLngList.forEach { latLngList ->
            latLngList.forEach { latLng ->
                latLngBuilder.include(latLng)
            }
        }
        areaBounds = latLngBuilder.build()
    }

    fun positionIsInsideWorkingArea(latLng: LatLng) : Boolean {
        workingAreaLatLngList.forEach { locationList ->
            if (PolyUtil.containsLocation(latLng, locationList, true)) {
                return true
            }
        }
        return false
    }
}