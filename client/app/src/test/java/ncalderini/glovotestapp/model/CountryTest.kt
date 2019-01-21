package ncalderini.glovotestapp.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CountryTest {
    @Test
    fun test_ar_countryCode_returnsArgentinaFlag() {
        val country = Country("AR", "Argentina")
        assertEquals("🇦🇷", country.flag())
    }

    @Test
    fun test_es_countryCode_returnsSpainFlag() {
        val country = Country("ES", "Spain")
        assertEquals("🇪🇸", country.flag())
    }

    //Complete with all available countries...
}