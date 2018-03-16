package io.github.treebricks.uubdc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.firestore.FirebaseFirestore
import io.github.treebricks.uubdc.Models.Donor
import kotlinx.android.synthetic.main.activity_donor_details.*


class DonorDetailsActivity : AppCompatActivity() {

    var donorId: String? = null
    private val mDocRef = FirebaseFirestore.getInstance()
    val donorsRef = mDocRef.collection("donors")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_details)
        setSupportActionBar(toolbarDonDetails)
        donorId = if(savedInstanceState == null) {
            intent.extras?.getString("DONOR_ID")
        }
        else {
            savedInstanceState.getSerializable("DONOR_ID") as String
        }

        val query = donorsRef.whereEqualTo("id", donorId)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {

                    val donor = Donor(
                            Id = document.get("id").toString(),
                            DonorName = document.get("donorName").toString(),
                            MobileNo = document.get("mobileNo").toString(),
                            Area = document.get("area").toString(),
                            BloodGroup = document.get("bloodGroup").toString(),
                            LastDonationDate = document.get("lastDonationDate").toString(),
                            Email = document.get("email").toString(),
                            TotalDonation = document.get("totalDonation").toString().toInt()
                    )

                    tvDonorName.text = donor.DonorName
                    tvBloodGroup.text = donor.BloodGroup
                    tvMobileNo.text = donor.MobileNo
                    tvArea.text = donor.Area
                    tvEmail.text = donor.Email
                    tvTotalDonation.text = donor.TotalDonation.toString();
                    tvLastDonationDate.text = donor.LastDonationDate

                }
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_donor_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId){
            R.id.action_delete -> {

                true
            }
            R.id.action_edit -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
