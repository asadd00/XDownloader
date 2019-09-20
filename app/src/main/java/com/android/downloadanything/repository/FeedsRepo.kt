package com.android.downloadanything.repository

import android.util.Log
import com.android.downloadanything.model.Feed
import com.android.downloadanything.repository.retUtils.RetUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedsRepo {
    private val tag = "ttt FeedsRepo"

    fun getFeeds(): ArrayList<Feed>?{
        var feedList: ArrayList<Feed>? = ArrayList()

        RetUtils.getAPIService().getRawData().enqueue(object : Callback<ArrayList<Feed>> {
            override fun onFailure(call: Call<ArrayList<Feed>>, t: Throwable) {
                Log.d(tag, "failure")
            }

            override fun onResponse(call: Call<ArrayList<Feed>>, response: Response<ArrayList<Feed>>) {
                if(response.isSuccessful && response.body() != null){
                    feedList = response.body()
                    Log.d(tag, "len: ${feedList?.size}")

                }
            }
        })

        return feedList
    }
}