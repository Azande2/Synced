package com.dianca.synced.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dianca.synced.R
import com.dianca.synced.models.MatchModel

class MatchAdapter(
    private val matches: List<MatchModel>,
    private val onClick: (MatchModel) -> Unit
) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    inner class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProfile: ImageView = view.findViewById(R.id.imgProfile)
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtBio: TextView = view.findViewById(R.id.txtBio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match_card, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]
        holder.txtName.text = "${match.name}, ${match.age}"
        holder.txtBio.text = match.bio
        Glide.with(holder.itemView.context)
            .load(match.imageUrl)
            .placeholder(R.drawable.default_avatar_foreground)
            .into(holder.imgProfile)

        holder.itemView.setOnClickListener { onClick(match) }
    }

    override fun getItemCount(): Int = matches.size
}
