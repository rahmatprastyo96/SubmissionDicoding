package com.example.submissiondicoding.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.submissiondicoding.adapter.FavoriteAdapter
import com.example.submissiondicoding.databinding.ActivityFavoriteBinding
import com.example.submissiondicoding.db.UserFavoriteHelper
import com.example.submissiondicoding.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: FavoriteAdapter

    private lateinit var rvUsersFavorite: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rvUsersFavorite = binding.rvUsersFavorite

        binding.progressBarUsersFavorite.visibility = View.INVISIBLE

        rvUsersFavorite.layoutManager = LinearLayoutManager(this)
        rvUsersFavorite.setHasFixedSize(true)

        loadFavoriteAsync()
    }

    private fun loadFavoriteAsync() {
        lifecycleScope.launch {
            binding.progressBarUsersFavorite.visibility = View.VISIBLE
            val favoriteHelper = UserFavoriteHelper.getInstance(applicationContext)
            favoriteHelper.open()

            val deferredNotes = async(Dispatchers.IO) {
                val cursor = favoriteHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }

            binding.progressBarUsersFavorite.visibility = View.INVISIBLE
            val favorite = deferredNotes.await()
            if (favorite.size > 0) {
                adapter = FavoriteAdapter(favorite)
                rvUsersFavorite.adapter = adapter

//                adapter.setOnItemClickCallback(object :
//                    FavoriteAdapter.OnItemClickCallback {
//                    override fun onItemClicked(data: UserFavorite) {
//                        val intent =
//                            Intent(this@FavoriteActivity, DetailUserActivity::class.java)
//                        intent.putExtra(DetailUserActivity.USER_DATA, data.username)
//                        startActivity(intent)
//                        Toast.makeText(
//                            this@FavoriteActivity,
//                            "${data.username}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                })

            } else {
                Log.d(TAG, "Tidak ada data saat ini")
            }

            favoriteHelper.close()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                //Write your logic here
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val TAG = FavoriteActivity::class.java.simpleName
    }

}