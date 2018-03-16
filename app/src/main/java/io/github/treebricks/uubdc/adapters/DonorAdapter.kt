package io.github.treebricks.uubdc.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.treebricks.uubdc.DonorDetailsActivity
import io.github.treebricks.uubdc.Models.Donor
import io.github.treebricks.uubdc.R

/**
 * Created by fahim on 6/3/18.
 */
class DonorAdapter(val context: Context, val donors: ArrayList<Donor>) : RecyclerView.Adapter<DonorAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val donorView = inflater.inflate(R.layout.donor_card_layout, parent, false)
        return ViewHolder(donorView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val donor = donors[position]
        holder.donorName.text = donor.DonorName
        holder.donorBloodGroup.text = donor.BloodGroup
        holder.lastDonationDate.text = donor.LastDonationDate
    }

    override fun getItemCount(): Int {
        return donors.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var donorName: TextView = itemView.findViewById(R.id.tvDonorName)
        var donorBloodGroup: TextView = itemView.findViewById(R.id.tvBloodGroup)
        var lastDonationDate: TextView = itemView.findViewById(R.id.tvLastDonationDate)
        var donorCard: CardView = itemView.findViewById(R.id.cv_donor_card)

        init {
            donorCard.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION) {
                    val donor = donors[position]
                    val intent = Intent(context, DonorDetailsActivity::class.java)
                    intent.putExtra("DONOR_ID", donor.Id)
                    context.startActivity(intent)
                }
            }
        }
    }
}