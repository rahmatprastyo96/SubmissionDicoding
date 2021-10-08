package com.example.submissiondicoding.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.submissiondicoding.network.ApiConfig
import com.example.submissiondicoding.network.ResponseDetail
import com.example.submissiondicoding.network.ResponseSearch
import com.example.submissiondicoding.R
import com.example.submissiondicoding.settings.SettingsActivity
import com.example.submissiondicoding.adapter.ListUserAdapter
import com.example.submissiondicoding.databinding.ActivityMainBinding
import com.example.submissiondicoding.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var rvUsers: RecyclerView
    private val listData = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvUsers = binding.rvUsers
        rvUsers.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this)
        rvUsers.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        rvUsers.addItemDecoration(itemDecoration)

        binding.progressBar.visibility = View.INVISIBLE

        binding.searchUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                listData.clear()
                endpointSearchUser(query)
                binding.searchUser.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

    }

    //Retrofit
    private fun endpointSearchUser(query: String) {
        val client = ApiConfig.getApiService().searchUsers(query)
        client.enqueue(object : Callback<ResponseSearch> {
            override fun onResponse(
                call: Call<ResponseSearch>,
                response: Response<ResponseSearch>
            ) {
                binding.progressBar.visibility = View.INVISIBLE
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        binding.emptyLayout.root.visibility = View.INVISIBLE
                        setReviewData(responseBody.items)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseSearch>, t: Throwable) {
                binding.progressBar.visibility = View.INVISIBLE
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun setReviewData(items: List<ResponseDetail>) {
        for (review in items) {
            val photo: String = review.avatarUrl.toString()
            val username: String = review.login.toString()

            listData.add(
                User(
                    photo, username
                )
            )
            val listUserAdapter = ListUserAdapter(listData)
            rvUsers.adapter = listUserAdapter
            listUserAdapter.setOnItemClickCallback(object :
                ListUserAdapter.OnItemClickCallback {
                override fun onItemClicked(data: User) {
                    val intent =
                        Intent(this@MainActivity, DetailUserActivity::class.java)
                    intent.putExtra(DetailUserActivity.USER_DATA, data.username)
                    startActivity(intent)
                    Toast.makeText(
                        this@MainActivity,
                        "${data.username}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favorite -> {
                val moveIntent = Intent(this@MainActivity, FavoriteActivity::class.java)
                startActivity(moveIntent)
            }

            R.id.action_settings -> {
                val moveIntent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(moveIntent)
            }

        }
        return super.onOptionsItemSelected(item)
    }



    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}