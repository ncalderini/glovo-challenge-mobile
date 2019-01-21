package ncalderini.glovotestapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class City(val code: String,
                val name: String,
                val country_code: String,
                val currency: String?,
                val enabled: Boolean,
                val busy: Boolean,
                val time_zone: String?,
                val language_code: String?,
                val working_area: List<String>,
                var workingAreaBounds: WorkingArea?) : Parcelable

