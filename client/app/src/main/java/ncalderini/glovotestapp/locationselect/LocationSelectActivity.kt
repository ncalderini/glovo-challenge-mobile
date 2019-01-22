package ncalderini.glovotestapp.locationselect

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_location_select.*
import ncalderini.glovotestapp.R
import ncalderini.glovotestapp.model.Country

class LocationSelectActivity : AppCompatActivity() {

    companion object {
        private const val ARG_COUNTRIES = "ARG_COUNTRIES"
        const val ARG_SELECTED_CITY = "ARG_SELECTED_CITY"

        fun getActivityIntent(context: Context, countries: List<Country>) : Intent {
            val citySelectorIntent = Intent(context, LocationSelectActivity::class.java)
            citySelectorIntent.putParcelableArrayListExtra(ARG_COUNTRIES, countries as ArrayList)
            return citySelectorIntent
        }
    }

    private lateinit var adapter: ExpandableCountryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_select)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val countries: List<Country> = intent.extras?.getParcelableArrayList(ARG_COUNTRIES)!!
        adapter = ExpandableCountryAdapter(countries)
        country_expandable_list.setAdapter(adapter)

        country_expandable_list.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val selectedCity = adapter.getChild(groupPosition, childPosition)
            setResult(Activity.RESULT_OK, Intent().putExtra(ARG_SELECTED_CITY, selectedCity))
            finish()

            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
