package ncalderini.glovotestapp.model

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WorkingAreaTest {

    private lateinit var workingArea: WorkingArea

    @Before
    fun doBefore() {
        val listLatLng = ArrayList<ArrayList<LatLng>>()
        val workingBounds = ArrayList<LatLng>()
        workingBounds.add(LatLng(-34.577131987965636, -58.44008124113941))
        workingBounds.add(LatLng(-34.57628, -58.43545))
        workingBounds.add(LatLng(-34.58248574829121, -58.435420989990234))
        workingBounds.add(LatLng(-34.577131987965636, -58.44008124113941))

        listLatLng.add(workingBounds)
        workingArea = WorkingArea(listLatLng)
    }

    @Test
    fun test_positionIsInsideWorkingArea_returns_true() {
        val insidePosition = LatLng(-34.57824, -58.43733)
        assertTrue(workingArea.positionIsInsideWorkingArea(insidePosition))
    }

    @Test
    fun test_positionIsOutsideWorkingArea_returns_false() {
        val outsidePosition = LatLng(-34.581773945577616, -58.44952261687183)
        assertFalse(workingArea.positionIsInsideWorkingArea(outsidePosition))
    }
}