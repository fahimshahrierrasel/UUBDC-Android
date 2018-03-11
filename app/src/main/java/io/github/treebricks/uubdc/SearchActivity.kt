package io.github.treebricks.uubdc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import io.github.treebricks.uubdc.Models.Donor
import io.github.treebricks.uubdc.adapters.DonorAdapter
import kotlinx.android.synthetic.main.activity_search.*
import android.widget.AdapterView.OnItemSelectedListener

/**
 * Enumeration Class for the search selection option
 */
enum class SearchOption{
    GROUP,
    NAME,
    MOBILE,
    AREA
}

class SearchActivity : AppCompatActivity() {

    private val mDocRef = FirebaseFirestore.getInstance()
    private var donors: ArrayList<Donor>? = null
    private var searchOption = SearchOption.GROUP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbarSrch)

        donors = ArrayList()

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
                        initializeRecyclerView(donors!!)
                    } else {
                        Log.w("Search Activity", "Error getting documents.", task.exception)
                    }
                }

        /**
         * Search option selection changed event
         */
        sSearchOption.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, item: View, position: Int, id: Long) {
                when (position)
                {
                    0 -> searchOption = SearchOption.GROUP
                    1 -> searchOption = SearchOption.NAME
                    2 -> searchOption = SearchOption.MOBILE
                    3 -> searchOption = SearchOption.AREA
                }
                searchDonors(etSearchText.text.toString())
            }
            override fun onNothingSelected(arg0: AdapterView<*>) {
                initializeRecyclerView(donors!!)
            }
        }

        /**
         * SearchText box text changed Event
         */
        etSearchText.onEditTextChanged { searchText ->
            searchDonors(searchText)
        }
    }

    /**
     * Search all Donor based on the search text and the selected search option
     *
     * @param searchText a string value by which donor will filtered
     */
    private fun searchDonors(searchText: String) {
        if(!searchText.isEmpty())
            initializeRecyclerView(getFilteredDonor(searchText, searchOption) as ArrayList<Donor>)
        else
            initializeRecyclerView(donors!!)
    }

    /**
     * Returns the list of Donor based on the search key and search option
     *
     * @param searchKey key value which will be matched with the Donor properties
     * @param searchOption Option for selecting the specific {@link Donor} Properties
     * @return List<Donor>? List of Donor will returned which can be empty
     */
    private fun getFilteredDonor(searchKey: String, searchOption: SearchOption): List<Donor>? {
        when(searchOption)
        {
            SearchOption.GROUP -> {
                return donors?.filter { d ->
                    d.BloodGroup.toLowerCase().contains(searchKey.toLowerCase())
                }
            }
            SearchOption.NAME -> {
                return donors?.filter {
                    d -> d.DonorName.toLowerCase().contains(searchKey.toLowerCase())
                }
            }
            SearchOption.MOBILE -> {
                return donors?.filter {
                    d -> d.MobileNo.toLowerCase().contains(searchKey.toLowerCase())
                }
            }
            SearchOption.AREA -> {
                return donors?.filter {
                    d -> d.Area.toLowerCase().contains(searchKey.toLowerCase())
                }
            }
        }
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
        val donorAdapter = DonorAdapter(this@SearchActivity, donors)
        rv_Donor.adapter = donorAdapter
        rv_Donor.layoutManager = LinearLayoutManager(this)
    }
}

/**
 * Extension of the EditText class of Text Changed Listener Event
 */
fun EditText.onEditTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s.toString())
        }

    })
}
