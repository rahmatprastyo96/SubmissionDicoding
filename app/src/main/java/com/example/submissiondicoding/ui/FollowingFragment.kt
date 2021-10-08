package com.example.submissiondicoding.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissiondicoding.BuildConfig
import com.example.submissiondicoding.ui.DetailUserActivity.Companion.USER_DATA
import com.example.submissiondicoding.network.ApiConfig
import com.example.submissiondicoding.network.ResponseDetail
import com.example.submissiondicoding.adapter.ListUserAdapter
import com.example.submissiondicoding.databinding.FragmentFollowingBinding
import com.example.submissiondicoding.model.User
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList


class FollowingFragment : Fragment() {

    private val secretKey = BuildConfig.KEY
    private var token: String = secretKey

    private var _binding: FragmentFollowingBinding? = null

    private val binding get() = _binding!!

    private val listUser = ArrayList<User>()
    private lateinit var adapter: ListUserAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ListUserAdapter(listUser)
        listUser.clear()
        binding.rvUsersFollowing.layoutManager = LinearLayoutManager(activity)
        getDataUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDataUser() {
        val username = requireActivity().intent.getStringExtra(USER_DATA).toString()
        endpointListFollowing(username)
    }

    private fun endpointListFollowing(usernameGithub: String) {
        binding.progressBarFollowing.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token  $token")
        val url = "https://api.github.com/users/$usernameGithub/following"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                binding.progressBarFollowing.visibility = View.INVISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val jsonArray = JSONArray(result)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val username: String = jsonObject.getString("login")
                        endpointDetailUser(username)
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                binding.progressBarFollowing.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    //Retrofit
    private fun endpointDetailUser(usernameGithub: String) {
        binding.progressBarFollowing.visibility = View.VISIBLE
        val client = ApiConfig.getApiService().userDetail(usernameGithub)
        client.enqueue(object : Callback<ResponseDetail> {
            override fun onResponse(
                call: Call<ResponseDetail>,
                response: Response<ResponseDetail>
            ) {
                binding.progressBarFollowing.visibility = View.INVISIBLE
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val photo: String = responseBody.avatarUrl.toString()
                        val username: String = responseBody.login.toString()
                        val name: String = responseBody.name.toString()
                        val company: String = responseBody.company.toString()
                        val location: String = responseBody.location.toString()
                        val repository: String = responseBody.publicRepos.toString()
                        val followers: String = responseBody.followers.toString()
                        val following: String = responseBody.following.toString()

                        listUser.add(
                            User(
                                photo, username, name, company, location, repository, followers, following
                            )
                        )
                        val listUserAdapter = ListUserAdapter(listUser)
                        binding.rvUsersFollowing.adapter = listUserAdapter
                        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: User) {}
                        })
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseDetail>, t: Throwable) {
                binding.progressBarFollowing.visibility = View.INVISIBLE
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        private val TAG = FollowingFragment::class.java.simpleName
    }
}