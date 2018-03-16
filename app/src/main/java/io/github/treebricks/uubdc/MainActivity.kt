package io.github.treebricks.uubdc

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import com.eggheadgames.aboutbox.AboutConfig
import com.eggheadgames.aboutbox.activity.AboutActivity
import com.google.firebase.firestore.FirebaseFirestore
import io.github.treebricks.uubdc.Models.Donor
import io.github.treebricks.uubdc.adapters.DonorAdapter
import kotlinx.android.synthetic.main.activity_main.*
import com.eggheadgames.aboutbox.IAnalytic
import com.eggheadgames.aboutbox.IDialog



class MainActivity : AppCompatActivity() {

    private val mDocRef = FirebaseFirestore.getInstance()
    private var donors: ArrayList<Donor>? = null

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val activityToolbar = toolbar

        // Initialize the material drawer
        drawer {
            toolbar = activityToolbar
            accountHeader {
                background = R.drawable.header
            }
            primaryItem("Add Donor") {
                icon = R.drawable.ic_person_add_black_24dp
                onClick { _ ->
                    startActivity(Intent(this@MainActivity, RegistrationActivity::class.java))
                    false
                }
            }
            primaryItem("Search Donor") {
                icon = R.drawable.ic_search_black_24dp
                onClick { _ ->
                    startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                    false
                }
            }
            primaryItem("Available Donor") {
                icon = R.drawable.ic_people_black_24dp
                onClick { _ ->
                    startActivity(Intent(this@MainActivity, AvailableDonorActivity::class.java))
                    false
                }
            }
            footer {
                primaryItem("Settings") {
                    icon = R.drawable.ic_settings_black_24dp
                    onClick { _ ->
                        startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        false
                    }
                }
                primaryItem("About") {
                    icon = R.drawable.ic_info_outline_black_24dp
                    onClick { _ ->
                        startAboutActivity()
                        false
                    }
                }
            }
        }

        donors = ArrayList()

        getAllDonors()

        swipeContainer.setOnRefreshListener {
            getAllDonors()
        }
    }

    private fun getAllDonors() {
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
                        }.forEach {
                            donors?.add(it)
                        }
                        initializeRecyclerView(donors!!)
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                    swipeContainer.isRefreshing = false
                }
    }

    private fun initializeRecyclerView(donors: ArrayList<Donor>) {
        val donorAdapter = DonorAdapter(this@MainActivity, donors)
        rvAllDonors.adapter = donorAdapter
        rvAllDonors.layoutManager = LinearLayoutManager(this)
    }

    private fun startAboutActivity()
    {
        val aboutConfig = AboutConfig.getInstance()
        aboutConfig.appName = getString(R.string.app_name)
        aboutConfig.appIcon = R.mipmap.ic_launcher
        aboutConfig.version = BuildConfig.VERSION_NAME

        aboutConfig.aboutLabelTitle = "Uttara University Blood Donation Club Donor Management Application"
        aboutConfig.packageName = applicationContext.packageName
        aboutConfig.buildType = AboutConfig.BuildType.GOOGLE

        aboutConfig.webHomePage = "https://treebricks.github.io/"

        // app publisher for "Try Other Apps" item
        aboutConfig.appPublisher = "treebricks"


        // Contact Support email details
        aboutConfig.emailAddress = "fahimshahrier2@gmail.com"
        aboutConfig.emailSubject = "UUBDC App"

        aboutConfig.privacyHtmlPath="https://treebricks.github.io/privacy_policy.html"
        aboutConfig.acknowledgmentHtmlPath = "https://treebricks.github.io/terms_condition.html"

        AboutActivity.launch(this)
    }
}
