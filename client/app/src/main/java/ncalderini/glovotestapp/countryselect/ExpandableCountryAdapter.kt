package ncalderini.glovotestapp.countryselect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.emoji.text.EmojiCompat
import androidx.emoji.widget.EmojiTextView
import ncalderini.glovotestapp.R
import ncalderini.glovotestapp.model.City
import ncalderini.glovotestapp.model.Country

class ExpandableCountryAdapter(private val countries: List<Country>) : BaseExpandableListAdapter() {

    override fun getGroup(groupPosition: Int): Country {
        return countries[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.country_item, null)
        }

        val tvCountry = convertView!!.findViewById<EmojiTextView>(R.id.country_tv)
        val country =  getGroup(groupPosition)
        tvCountry.text =  EmojiCompat.get().process(country.flag() + " " + country.name)

        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return countries[groupPosition].cities.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): City {
        return countries[groupPosition].cities[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.city_item, null)
        }

        val cityTextView = convertView!!.findViewById<TextView>(R.id.city_tv)
        val city = getChild(groupPosition, childPosition)
        cityTextView.text = city.name

        return convertView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return countries.size
    }
}