package io.github.treebricks.uubdc

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_registration.*
import java.text.SimpleDateFormat
import java.util.*





class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

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

    }

}
