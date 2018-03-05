package io.github.treebricks.uubdc

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import com.google.firebase.firestore.FirebaseFirestore
import io.github.treebricks.uubdc.Models.Donor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val mDocRef = FirebaseFirestore.getInstance()

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val activityToolbar = toolbar

        // Initialize the material drawer
        drawer {
            toolbar = activityToolbar
            accountHeader{
                background = R.drawable.header
            }
            primaryItem("Add Donor"){
                onClick { _ ->
                    startActivity(Intent(this@MainActivity, RegistrationActivity::class.java))
                    false
                }
            }
            primaryItem("Search Donor"){
                onClick { _ ->
                    startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                    false
                }
            }
            primaryItem("Available Donor"){
                onClick { _ ->
                    startActivity(Intent(this@MainActivity, DonorDetailsActivity::class.java))
                    false
                }
            }
            footer {
                primaryItem("About"){
                    onClick { _ ->

                        false
                    }
                }
            }
        }

        // Firestore data fetch
        mDocRef.collection("donors")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val adonor = Donor(
                                    DonorName = document.get("donorName").toString(),
                                    MobileNo = document.get("mobileNo").toString(),
                                    Area = document.get("area").toString(),
                                    BloodGroup = document.get("bloodGroup").toString(),
                                    LastDonationDate = document.get("lastDonationDate").toString(),
                                    Email = document.get("email").toString(),
                                    Donations = null
                            )
                            Log.d(TAG, adonor.toString())
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
    }
}
