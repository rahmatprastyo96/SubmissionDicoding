package com.example.submissiondicoding.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.submissiondicoding.R
import com.example.submissiondicoding.adapter.FavoriteAdapter
import com.example.submissiondicoding.databinding.ActivityFavoriteBinding
import com.example.submissiondicoding.db.UserFavoriteHelper
import com.example.submissiondicoding.helper.MappingHelper
import com.example.submissiondicoding.settings.SettingsActivity
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
            } else {
                Log.d(TAG, "Tidak ada data saat ini")
            }
            favoriteHelper.close()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val moveIntent = Intent(this@FavoriteActivity, MainActivity::class.java)
                startActivity(moveIntent)
            }
            R.id.action_settings -> {
                val moveIntent = Intent(this@FavoriteActivity, SettingsActivity::class.java)
                startActivity(moveIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val TAG = FavoriteActivity::class.java.simpleName
    }

}