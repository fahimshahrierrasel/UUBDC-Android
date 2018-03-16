package io.github.treebricks.uubdc

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_update.*
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.firestore.FirebaseFirestore
import io.github.treebricks.uubdc.Models.Donor


class UpdateActivity : AppCompatActivity() {

    // Access a Cloud Firestore instance from your Activity
    private var donorId: String? = null
    private val mDocRef = FirebaseFirestore.getInstance()
    private val donorsRef = mDocRef.collection("donors")
    private var donor: Donor? = null
    private var documentId: String? = null

    private val TAG = UpdateActivity::javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        setSupportActionBar(toolbarReg)


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
                    documentId = document.id

                    donor = Donor(
                            Id = document.get("id").toString(),
                            DonorName = document.get("donorName").toString(),
                            MobileNo = document.get("mobileNo").toString(),
                            Area = document.get("area").toString(),
                            BloodGroup = document.get("bloodGroup").toString(),
                            LastDonationDate = document.get("lastDonationDate").toString(),
                            Email = document.get("email").toString(),
                            TotalDonation = document.get("totalDonation").toString().toInt()
                    )

                    val bloodGroup = resources.getStringArray(R.array.blood_groups).toList()

                    etUDonorName.setText(donor?.DonorName)
                    sUBloodGroup.setSelection(bloodGroup.indexOf(donor?.BloodGroup))
                    etUMobileNo.setText(donor?.MobileNo)
                    etUArea.setText(donor?.Area)
                    etUDonorEmail.setText(donor?.Email)
                    etULastDonationDate.setText(donor?.LastDonationDate)
                }
            }
        }

        // Set Current Date To Last Donation Date
        // Initialize Calendar
        val myCalendar = Calendar.getInstance()
        val dateFormat = "dd-MM-yyyy"
        // Date Format
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US)

        // Set calendar date and update editDate
        val datePickerOnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            etULastDonationDate.setText(simpleDateFormat.format(myCalendar.time))
        }

        // LastDonationDate click listener
        etULastDonationDate.setOnClickListener {
            DatePickerDialog(this@UpdateActivity, datePickerOnDateSetListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH))
                    .show()
        }

        bUpdate.setOnClickListener { _ ->
            val donorName = etUDonorName.text.toString()
            val donorEmail = etUDonorEmail.text.toString()
            val donorMobile = etUMobileNo.text.toString()
            val bloodGroup = sUBloodGroup.selectedItem.toString()
            val area = etUArea.text.toString()
            val lastDonationDate = etULastDonationDate.text.toString()

            var validationFlag = true

            if(donorName.isEmpty()){
                etUDonorName.error = "Donor name is required"
                validationFlag = false
            }
            if(donorMobile.isEmpty()){
                etUMobileNo.error = "Mobile no is required"
                validationFlag = false
            }
            if(area.isEmpty()) {
                etUArea.error = "Area is required"
                validationFlag = false
            }

            if(validationFlag)
            {

                val updatedDonor = Donor(donor!!.Id, donorName, donorEmail, donorMobile, bloodGroup, area,
                        lastDonationDate, donor!!.TotalDonation)

                donorsRef.document(documentId!!)
                        .set(updatedDonor)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this@UpdateActivity, "Donor update successful!", Toast.LENGTH_SHORT).show()
                            finish()
                        }.addOnFailureListener { exception ->
                            Log.w(TAG, "Error adding Document: ", exception)
                            Toast.makeText(this@UpdateActivity, "Donor update failed!", Toast.LENGTH_SHORT).show()
                        }
            }
        }

    }

}
