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
import com.afollestad.materialdialogs.MaterialDialog
import com.eggheadgames.aboutbox.AboutConfig
import com.eggheadgames.aboutbox.activity.AboutActivity
import com.google.firebase.firestore.FirebaseFirestore
import io.github.treebricks.uubdc.Models.Donor
import io.github.treebricks.uubdc.adapters.DonorAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mDocRef = FirebaseFirestore.getInstance()
    private var donors: ArrayList<Donor>? = null

    private val tag = "MainActivity"
    private var dialog: MaterialDialog? = null

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

        showWaitDialog()

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
                        Log.w(tag, "Error getting documents.", task.exception)
                    }
                    swipeContainer.isRefreshing = false
                    dialog?.dismiss()
                    dialog?.cancel()
                    dialog = null
                }
    }

    private fun initializeRecyclerView(donors: ArrayList<Donor>) {
        val donorAdapter = DonorAdapter(this@MainActivity, donors)
        rvAllDonors.adapter = donorAdapter
        rvAllDonors.layoutManager = LinearLayoutManager(this@MainActivity)
    }

    private fun showWaitDialog()
    {
        dialog = MaterialDialog.Builder(this@MainActivity)
                .title("Please Wait...")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .cancelable(false)
                .show()
    }

    private fun startAboutActivity()
    {
        val aboutConfig = AboutConfig.getInstance()
        aboutConfig.appName = getString(R.string.app_name)
        aboutConfig.appIcon = R.mipmap.ic_launcher
        aboutConfig.version = BuildConfig.VERSION_NAME
        aboutConfig.extraTitle="UUBDC is an open source app"
        aboutConfig.extra="www.github.com/fahimshahrierrasel/UUBDC-Android"

        aboutConfig.aboutLabelTitle = "Uttara University Blood Donation Club Donor Management Application"
        aboutConfig.appPublisher = "treebricks"
        aboutConfig.packageName = applicationContext.packageName
        aboutConfig.buildType = AboutConfig.BuildType.GOOGLE

        aboutConfig.webHomePage = "https://treebricks.github.io/"

        aboutConfig.emailAddress = "fahimshahrier2@gmail.com"
        aboutConfig.emailSubject = "UUBDC Android App"


        aboutConfig.privacyHtmlPath="https://treebricks.github.io/privacy_policy.html"
        aboutConfig.acknowledgmentHtmlPath = "https://treebricks.github.io/terms_condition.html"

        AboutActivity.launch(this)
    }
}
