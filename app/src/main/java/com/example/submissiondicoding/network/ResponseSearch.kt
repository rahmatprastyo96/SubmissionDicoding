package com.example.submissiondicoding.network

import com.google.gson.annotations.SerializedName

data class ResponseSearch(

    @SerializedName("items")
    val items: List<ResponseDetail>

)