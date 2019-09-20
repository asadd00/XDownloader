package com.android.downloadanything.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.downloadanything.model.Feed
import com.android.downloadanything.repository.retUtils.RetUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedsRepository {
    private val tag = "ttt FeedsRepository"

    fun getFeeds(): MutableLiveData<ArrayList<Feed>>{
        val feedList: MutableLiveData<ArrayList<Feed>> = MutableLiveData()
        RetUtils.getAPIService().getRawData().enqueue(object : Callback<ArrayList<Feed>> {
            override fun onFailure(call: Call<ArrayList<Feed>>, t: Throwable) {
                Log.d(tag, "failure")
                feedList.value = null
            }

            override fun onResponse(call: Call<ArrayList<Feed>>, response: Response<ArrayList<Feed>>) {
                if(response.isSuccessful && response.body() != null){
                    feedList.value = response.body()
                    Log.d(tag, "len: ${feedList.value?.size}")

                }
            }
        })

        return feedList
    }
}