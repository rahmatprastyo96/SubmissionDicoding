package com.example.submissiondicoding.db

import android.provider.BaseColumns

internal class DatabaseContract {

    internal class UserColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite"
            const val _ID = "_id"
            const val PHOTO = "photo"
            const val USERNAME = "username"
        }
    }
}