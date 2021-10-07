package com.example.submissiondicoding.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.submissiondicoding.R
import com.example.submissiondicoding.databinding.ItemRowUserBinding
import com.example.submissiondicoding.model.User
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ListUserAdapter(private val listUser: ArrayList<User>) : RecyclerView.Adapter<ListUserAdapter.ListViewHolder>(){
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_row_user, viewGroup, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (photo, username) = listUser[position]
        Glide.with(holder.itemView.context)
            .load(photo)
            .apply(RequestOptions().override(250, 250))
            .into(holder.imgPhoto)
        holder.txtUsername.text = StringBuilder("Username: \n").append(username)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listUser[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listUser.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val binding = ItemRowUserBinding.bind(itemView)
            var imgPhoto: CircleImageView = binding.imgItemPhoto
            var txtUsername: TextView = binding.tvItemUsername
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
    }

}