package com.example.submissiondicoding.helper

import android.database.Cursor
import com.example.submissiondicoding.db.DatabaseContract
import com.example.submissiondicoding.model.entity.UserFavorite

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<UserFavorite> {
        val userList = ArrayList<UserFavorite>()
        notesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.UserColumns._ID))
                val photo = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.PHOTO))
                val username = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.USERNAME))
                userList.add(UserFavorite(id, username, photo))
            }
        }
        return userList
    }

}