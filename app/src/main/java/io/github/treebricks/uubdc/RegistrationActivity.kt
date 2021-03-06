package io.github.treebricks.uubdc

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_registration.*
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.firestore.FirebaseFirestore
import io.github.treebricks.uubdc.Models.Donor


class RegistrationActivity : AppCompatActivity() {

    // Access a Cloud Firestore instance from your Activity
    private var db = FirebaseFirestore.getInstance()

    private val tag = RegistrationActivity::javaClass.name
    private var dialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        setSupportActionBar(toolbarReg)

        // Set Current Date To Last Donation Date
        // Initialize Calendar
        val myCalendar = Calendar.getInstance()
        val dateFormat = "dd-MM-yyyy"
        // Date Format
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US)
        // Set Current Date
        val currentDate = System.currentTimeMillis()
        val dateString = simpleDateFormat.format(currentDate)
        etLastDonationDate.setText(dateString)

        // Set calendar date and update editDate
        val datePickerOnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            etLastDonationDate.setText(simpleDateFormat.format(myCalendar.time))
        }

        // LastDonationDate click listener
        etLastDonationDate.setOnClickListener {
            DatePickerDialog(this@RegistrationActivity, datePickerOnDateSetListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH))
                    .show()
        }

        bRegister.setOnClickListener { _ ->

            showWaitDialog()

            val donorName = etDonorName.text.toString()
            val donorEmail = etDonorEmail.text.toString()
            val donorMobile = etMobileNo.text.toString()
            val bloodGroup = sBloodGroup.selectedItem.toString()
            val area = etArea.text.toString()
            val lastDonationDate = etLastDonationDate.text.toString()

            var validationFlag = true

            if (donorName.isEmpty()) {
                etDonorName.error = "Donor name is required"
                validationFlag = false
            }
            if (donorMobile.isEmpty()) {
                etMobileNo.error = "Mobile no is required"
                validationFlag = false
            }
            if (area.isEmpty()) {
                etArea.error = "Area is required"
                validationFlag = false
            }

            if (validationFlag) {

                val newDonor = Donor(UUID.randomUUID().toString(), donorName, donorEmail, donorMobile, bloodGroup, area,
                        lastDonationDate, 1)

                db.collection("donors")
                        .add(newDonor)
                        .addOnSuccessListener { _ ->
                            Toast.makeText(this@RegistrationActivity, "Donor registration successfully!", Toast.LENGTH_SHORT).show()
                            dialog?.dismiss()
                            dialog?.cancel()
                            dialog = null
                            finish()
                        }.addOnFailureListener { exception ->
                            dialog?.dismiss()
                            dialog?.cancel()
                            dialog = null

                            Log.w(tag, "Error adding Document: ", exception)
                            Toast.makeText(this@RegistrationActivity, "Donor registration failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun showWaitDialog() {
        dialog = MaterialDialog.Builder(this@RegistrationActivity)
                .title("Please Wait...")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .cancelable(false)
                .show()
    }

}
