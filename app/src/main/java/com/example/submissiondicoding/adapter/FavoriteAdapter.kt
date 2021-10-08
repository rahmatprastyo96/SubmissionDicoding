package com.example.submissiondicoding.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.submissiondicoding.ui.DetailUserActivity
import com.example.submissiondicoding.R
import com.example.submissiondicoding.databinding.ItemRowUserBinding
import com.example.submissiondicoding.model.entity.UserFavorite
import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList

class FavoriteAdapter(private val listUser: ArrayList<UserFavorite>) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    fun addItem(favorite: UserFavorite) {
        this.listUser.add(favorite)
        notifyItemInserted(this.listUser.size - 1)
    }

    fun updateItem(position: Int, favorite: UserFavorite) {
        this.listUser[position] = favorite
        notifyItemChanged(position, favorite)
    }

    fun removeItem(position: Int) {
        this.listUser.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.listUser.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_user, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val (id, username, photo) = listUser[position]

        holder.txtUsername.text = StringBuilder("Username: \n").append(username)
        Glide.with(holder.itemView.context)
            .load(photo)
            .apply(RequestOptions().override(250, 250))
            .into(holder.imgPhoto)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, DetailUserActivity::class.java)
            intent.putExtra(DetailUserActivity.USER_DATA, username)
            intent.putExtra(DetailUserActivity.EXTRA_POSITION, id)
            it.context.startActivity(intent)
        }

    }
    override fun getItemCount(): Int = this.listUser.size

    inner class FavoriteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val binding = ItemRowUserBinding.bind(itemView)
        var txtUsername: TextView = binding.tvItemUsername
        var imgPhoto: CircleImageView = binding.imgItemPhoto

    }

}