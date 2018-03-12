package io.github.treebricks.uubdc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.google.firebase.firestore.FirebaseFirestore
import io.github.treebricks.uubdc.Models.Donor
import io.github.treebricks.uubdc.adapters.DonorAdapter
import kotlinx.android.synthetic.main.activity_available_donor.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.preference.PreferenceManager

class AvailableDonorActivity : AppCompatActivity() {

    private val mDocRef = FirebaseFirestore.getInstance()
    private var donors: ArrayList<Donor>? = null
    private var dayDifference = 80

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_available_donor)
        setSupportActionBar(toolbarAvailable)

        donors = ArrayList()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        dayDifference = Integer.parseInt(prefs.getString("donation_frequency", "80"))

        /**
         * Fetch the Documents from FireStore database
         */
        mDocRef.collection("donors")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val donor = Donor(
                                    DonorName = document.get("donorName").toString(),
                                    MobileNo = document.get("mobileNo").toString(),
                                    Area = document.get("area").toString(),
                                    BloodGroup = document.get("bloodGroup").toString(),
                                    LastDonationDate = document.get("lastDonationDate").toString(),
                                    Email = document.get("email").toString(),
                                    Donations = null
                            )
                            donors?.add(donor)
                        }
                        searchDonors(sGroupSelection.selectedItem.toString())
                    } else {
                        Log.w("Search Activity", "Error getting documents.", task.exception)
                    }
                }

        sGroupSelection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, item: View, position: Int, id: Long) {
                searchDonors(sGroupSelection.selectedItem.toString())
            }
            override fun onNothingSelected(arg0: AdapterView<*>) {
                initializeRecyclerView(donors!!)
            }
        }
    }

    private fun searchDonors(searchText: String) {
        val filteredDonor = ArrayList<Donor>()

        donors!!.forEach { donor ->
            if( donationDateDifference(donor.LastDonationDate!!) > dayDifference && donor.BloodGroup == searchText)
                filteredDonor.add(donor)
        }
        initializeRecyclerView(filteredDonor)
    }
    private fun donationDateDifference(lastDonationDate: String): Long {

        val calendar = Calendar.getInstance()
        val dates = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val sCurrentDate = dates.format(calendar.time)
        val currentDate = dates.parse(sCurrentDate)
        val nextDate = dates.parse(lastDonationDate)
        val difference = currentDate.time - nextDate.time
        return difference / (24 * 60 * 60 * 1000)
    }

    /**
     * Populate the RecyclerView for Donor List
     * <p>
     * This method initialize the RecyclerView Adapter using the Donor list and
     * populate the RecyclerView
     *
     * @param donors list of Donors
     */
    private fun initializeRecyclerView(donors: ArrayList<Donor>) {
        val donorAdapter = DonorAdapter(this@AvailableDonorActivity, donors)
        rv_available_donor.adapter = donorAdapter
        rv_available_donor.layoutManager = LinearLayoutManager(this)
    }
}
