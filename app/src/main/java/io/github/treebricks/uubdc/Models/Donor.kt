package io.github.treebricks.uubdc.Models

import java.util.*

/**
 * Created by fahim on 6/3/18.
 */
data class Donor (val Id: String, val DonorName: String, val Email: String?, val MobileNo: String,
                  val BloodGroup: String, val Area: String, val LastDonationDate: String?,
                  var TotalDonation: Int)