package ncalderini.glovotestapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Country(val code: String,
                   val name: String,
                   var cities: List<City> = emptyList()) : Parcelable {

    /**
     * @return Returns an Emoji, in Unicode format, representing the flag of this Country
     */
    fun flag() : String {
        val flagOffset = 0x1F1E6
        val asciiOffset = 0x41

        val firstChar = Character.codePointAt(code, 0) - asciiOffset + flagOffset
        val secondChar = Character.codePointAt(code, 1) - asciiOffset + flagOffset

        return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
    }
}