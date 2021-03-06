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
import com.afollestad.materialdialogs.MaterialDialog

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

    private var dialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbarSrch)

        donors = ArrayList()

        showWaitDialog()

        getAllDonors()


        swipeContainer.setOnRefreshListener {
            getAllDonors()
        }

        /**
         * Search option selection changed event
         */
        sGroupSelection.onItemSelectedListener = object : OnItemSelectedListener {
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

    private fun getAllDonors() {
        /**
         * Fetch the Documents from FireStore database
         */
        mDocRef.collection("donors")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        donors?.clear()
                        task.result.map {
                                    Donor(
                                            Id = it.get("id").toString(),
                                            DonorName = it.get("donorName").toString(),
                                            MobileNo = it.get("mobileNo").toString(),
                                            Area = it.get("area").toString(),
                                            BloodGroup = it.get("bloodGroup").toString(),
                                            LastDonationDate = it.get("lastDonationDate").toString(),
                                            Email = it.get("email").toString(),
                                            TotalDonation = it.get("totalDonation").toString().toInt()
                                    )
                                }
                                .forEach {
                                    donors?.add(it)
                                }
                        initializeRecyclerView(donors!!)
                    } else {
                        Log.w("Search Activity", "Error getting documents.", task.exception)
                    }
                    swipeContainer.isRefreshing = false
                    dialog?.dismiss()
                    dialog?.cancel()
                    dialog = null
                }
    }

    private fun showWaitDialog()
    {
        dialog = MaterialDialog.Builder(this@SearchActivity)
                .title("Please Wait...")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .cancelable(false)
                .show()
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
        rv_donor.adapter = donorAdapter
        rv_donor.layoutManager = LinearLayoutManager(this@SearchActivity)
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
