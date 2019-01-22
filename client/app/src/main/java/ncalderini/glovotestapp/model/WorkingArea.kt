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
        workingAreaLatLngList.isEmpty()
        areaBounds = latLngBuilder.build()
    }

    /**
     * @param latLng Position to be queried.
     * @return `true` if the position is inside the Working Area bounds. `false` otherwise
     */
    fun positionIsInsideWorkingArea(latLng: LatLng) : Boolean {
        workingAreaLatLngList.forEach { locationList ->
            if (PolyUtil.containsLocation(latLng, locationList, true)) {
                return true
            }
        }
        return false
    }
}