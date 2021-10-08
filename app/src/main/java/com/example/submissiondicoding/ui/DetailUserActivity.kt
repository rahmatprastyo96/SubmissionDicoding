package com.example.submissiondicoding.ui

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.submissiondicoding.network.ApiConfig
import com.example.submissiondicoding.network.ResponseDetail
import com.example.submissiondicoding.R
import com.example.submissiondicoding.adapter.SectionsPagerAdapter
import com.example.submissiondicoding.databinding.ActivityDetailUserBinding
import com.example.submissiondicoding.db.DatabaseContract
import com.example.submissiondicoding.db.UserFavoriteHelper
import com.example.submissiondicoding.helper.MappingHelper
import com.example.submissiondicoding.model.User
import com.example.submissiondicoding.settings.SettingsActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailUserActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailUserBinding

    private val listData = ArrayList<User>()

    private var id: Int? = null
    private lateinit var usersFavoriteHelper: UserFavoriteHelper
    private var isFavorite: Boolean = false
    private var favoriteIsTrue: Boolean = false

    private lateinit var username: String
    private lateinit var photo: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        username  = intent.getStringExtra(USER_DATA).toString()

        val favoriteHelper = UserFavoriteHelper.getInstance(applicationContext)
        favoriteHelper.open()
        val cursor = favoriteHelper.queryByUsername(username)
        val favorite =  MappingHelper.mapCursorToArrayList(cursor)
        if (favorite.size > 0) {
            favoriteIsTrue = true
        }
        favoriteHelper.close()

        isFavorite = favoriteIsTrue
        if (isFavorite) {
            id = intent.getIntExtra(EXTRA_POSITION, -1)
            val favorite: Int = R.drawable.ic_favorite
            binding.favorite.setImageResource(favorite)
        } else {
            val unFavorite: Int = R.drawable.ic_favorite_border
            binding.favorite.setImageResource(unFavorite)
        }

        binding.favorite.setOnClickListener(this)

        endpointDetailUser(username)
        viewPagerAdapterConfig()

    }

    override fun onClick(view: View) {
        val favorite: Int = R.drawable.ic_favorite
        val unFavorite: Int = R.drawable.ic_favorite_border

        if (view.id == R.id.favorite) {
            usersFavoriteHelper = UserFavoriteHelper.getInstance(applicationContext)
            usersFavoriteHelper.open()

            if (isFavorite) {
                binding.favorite.setImageResource(unFavorite)
                isFavorite = false

                val result = usersFavoriteHelper.deleteById(id.toString()).toLong()

                if (result > 0) {

                    Toast.makeText(
                        this@DetailUserActivity,
                        "Un Favorite",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@DetailUserActivity,
                        "Favorite",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else {
                binding.favorite.setImageResource(favorite)
                isFavorite = true

                val values = ContentValues()
                values.put(DatabaseContract.UserColumns.PHOTO, photo)
                values.put(DatabaseContract.UserColumns.USERNAME, username)

                val result = usersFavoriteHelper.insert(values)

                if (result > 0) {
                    id = result.toInt()
                    Toast.makeText(
                        this@DetailUserActivity,
                        "Favorite",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@DetailUserActivity,
                        "Un Favorite",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            usersFavoriteHelper.close()
        }
    }

    private fun viewPagerAdapterConfig() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f
    }


    //Retrofit
    private fun endpointDetailUser(usernameGithub: String) {
        val client = ApiConfig.getApiService().userDetail(usernameGithub)
        client.enqueue(object : Callback<ResponseDetail> {
            override fun onResponse(
                call: Call<ResponseDetail>,
                response: Response<ResponseDetail>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        photo = responseBody.avatarUrl.toString()
                        username = responseBody.login.toString()
                        val name: String = responseBody.name.toString()
                        val company: String = responseBody.company.toString()
                        val location: String = responseBody.location.toString()
                        val repository: String = responseBody.publicRepos.toString()
                        val followers: String = responseBody.followers.toString()
                        val following: String = responseBody.following.toString()

                        listData.add(
                            User(
                                photo, username, name, company, location, repository, followers, following
                            )
                        )
                        with(binding) {
                            tvDetailUsername.text = StringBuilder(  "Username: ").append(username)
                            tvDetailName.text = StringBuilder(  "Name: ").append(name)
                            tvDetailCompany.text = StringBuilder(  "Company: ").append(company)
                            tvDetailLocation.text = StringBuilder(  "Location: ").append(username)
                            tvDetailRepository.text = StringBuilder(  "Repository \n").append(repository)
                            tvDetailFollowers.text = StringBuilder(  "Followers \n").append(followers)
                            tvDetailFollowing.text = StringBuilder(  "Following \n").append(following)
                            Glide.with(root.context)
                                .load(photo)
                                .into(imgPhoto)
                        }

                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ResponseDetail>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                //Write your logic here
                finish()
                true
            }
            R.id.action_settings -> {
                val moveIntent = Intent(this@DetailUserActivity, SettingsActivity::class.java)
                startActivity(moveIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object{
        const val USER_DATA = "user_data"
        const val EXTRA_POSITION = "extra_position"

        private val TAG = DetailUserActivity::class.java.simpleName
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

}