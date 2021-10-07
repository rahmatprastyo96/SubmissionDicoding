package com.example.submissiondicoding.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserFavorite(
    var id: Int = 0,
    var username: String? = null,
    var photo: String? = null,
) : Parcelable
