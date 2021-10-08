package com.example.submissiondicoding.network


import retrofit2.Call
import retrofit2.http.*

interface ApiService{

    @GET("search/users")
    fun searchUsers(
        @Query("q") q: String
    ): Call<ResponseSearch>

    @GET("users/{username}")
    fun userDetail(
        @Path("username") username: String?
    ): Call<ResponseDetail>

}

