package com.example.submissiondicoding.Network

import com.google.gson.annotations.SerializedName

data class ResponseSearch(

    @SerializedName("items")
    val items: List<ResponseDetail>

)