package io.github.treebricks.uubdc

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.ramotion.circlemenu.CircleMenuView
import io.github.treebricks.uubdc.Models.Donor
import kotlinx.android.synthetic.main.activity_donor_details.*
import kotlinx.android.synthetic.main.content_donor_details.*
import java.text.SimpleDateFormat
import java.util.*


class DonorDetailsActivity : AppCompatActivity() {

    private var donorId: String? = null
    private val mDocRef = FirebaseFirestore.getInstance()
    private val donorsRef = mDocRef.collection("donors")
    private var donor: Donor? = null
    private var message: String = " "
    private var documentId: String? = null

    companion object {
        val CALL_REQUEST_CODE: Int = 1
        val SMS_REQUEST_CODE: Int = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_details)
        setSupportActionBar(toolbarDonorDetails)
        toolbar_layout.isTitleEnabled = false
        supportActionBar?.setDisplayShowTitleEnabled(false)


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

                    tvDonorName.text = getString(R.string.donor_name_plc, donor?.DonorName)
                    tvBloodGroup.text = getString(R.string.blood_group_plc, donor?.BloodGroup)
                    tvMobileNo.text = getString(R.string.mobile_no_plc,donor?.MobileNo)
                    tvArea.text = getString(R.string.area_plc,donor?.Area)
                    tvEmail.text = getString(R.string.email_plc,donor?.Email)
                    tvTotalDonation.text = getString(R.string.total_donation_plc,donor?.TotalDonation.toString())
                    tvLastDonationDate.text = getString(R.string.last_donation_date_plc,donor?.LastDonationDate)
                }
                message = "Hello, ${donor?.DonorName}. Are you well?"
            }
        }

        val dateFormat = "dd-MM-yyyy"
        // Date Format
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US)
        // Set Current Date
        val currentDate = System.currentTimeMillis()
        val dateString = simpleDateFormat.format(currentDate)

        circle_menu.eventListener=object : CircleMenuView.EventListener(){
            override fun onButtonClickAnimationEnd(view: CircleMenuView, buttonIndex: Int) {
                when(buttonIndex)
                {
                    0 -> {
                        MaterialDialog.Builder(this@DonorDetailsActivity)
                                .title("New Donation Date")
                                .content("New donation date for the donor. (dd-mm-yyyy)")
                                .inputType(InputType.TYPE_CLASS_DATETIME)
                                .input("Donation Date", dateString, { _, input ->
                                    donorsRef.document(documentId!!)
                                            .set(Donor(
                                                    Id = donor!!.Id,
                                                    DonorName = donor!!.DonorName,
                                                    MobileNo = donor!!.MobileNo,
                                                    Area = donor!!.Area,
                                                    BloodGroup = donor!!.BloodGroup,
                                                    LastDonationDate = input.toString(),
                                                    Email = donor!!.Email,
                                                    TotalDonation = donor!!.TotalDonation + 1
                                            ))
                                            .addOnSuccessListener {
                                                Toast.makeText(this@DonorDetailsActivity, "Donation Add :)", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this@DonorDetailsActivity, "Donation Failed!!!", Toast.LENGTH_SHORT).show()
                                            }
                                }).show()
                    }
                    // Call
                    1 -> {
                        val callIntent = Intent(Intent.ACTION_CALL)
                        callIntent.data = Uri.parse("tel:${donor?.MobileNo}")

                        if (ActivityCompat.checkSelfPermission(this@DonorDetailsActivity, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this@DonorDetailsActivity, arrayOf(android.Manifest.permission.CALL_PHONE), CALL_REQUEST_CODE)
                        } else {
                            startActivity(callIntent)
                        }
                    }
                    //SMS
                    2 -> {
                        val smsIntent = Intent(Intent.ACTION_VIEW)
                        smsIntent.data = Uri.parse("sms:${donor?.MobileNo}")
                        smsIntent.putExtra("sms_body", message)
                        if (ActivityCompat.checkSelfPermission(this@DonorDetailsActivity, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this@DonorDetailsActivity, arrayOf(android.Manifest.permission.SEND_SMS), SMS_REQUEST_CODE)
                        } else {
                            startActivity(smsIntent)
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
            //Call
                1 -> {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel:${donor?.MobileNo}")
                    if (ActivityCompat.checkSelfPermission(this@DonorDetailsActivity, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        startActivity(callIntent)
                    }
                }
            //SMS
                2 -> {
                    val smsIntent = Intent(Intent.ACTION_VIEW)
                    smsIntent.data = Uri.parse("sms:${donor?.MobileNo}")
                    smsIntent.putExtra("sms_body", message)
                    if (ActivityCompat.checkSelfPermission(this@DonorDetailsActivity, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        startActivity(smsIntent)
                    }
                }
            }
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_donor_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId){
            R.id.action_delete -> {
                donorsRef.document(documentId!!).delete()
                        .addOnSuccessListener {
                            Toast.makeText(this@DonorDetailsActivity, "Donor Deleted :(", Toast.LENGTH_SHORT).show()
                            finish()
                            startActivity(Intent(this@DonorDetailsActivity, MainActivity::class.java))
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@DonorDetailsActivity, "Donor deletion failed :(", Toast.LENGTH_SHORT).show()
                        }
                true
            }
            R.id.action_edit -> {
                val intent = Intent(this@DonorDetailsActivity, UpdateActivity::class.java)
                intent.putExtra("DONOR_ID", donor!!.Id)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
