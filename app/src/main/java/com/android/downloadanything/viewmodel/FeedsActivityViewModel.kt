package com.android.downloadanything.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.downloadanything.model.Feed
import com.android.downloadanything.repository.FeedsRepo

class FeedsActivityViewModel: ViewModel() {
    private val tag = "ttt FeedsActivityViewM"
    private lateinit var mFeedList: MutableLiveData<ArrayList<Feed>>
    private lateinit var feedsRepo: FeedsRepo

    fun init(){
        feedsRepo = FeedsRepo()
        mFeedList = MutableLiveData()
        val feedList = ArrayList<Feed>()
        mFeedList.value = feedList
    }

    fun getFeeds(): LiveData<ArrayList<Feed>>{
        return mFeedList
    }

    fun getFeedsFromRepo(){
        Log.v(tag, "getFeedsFromRepo")
        mFeedList.value = feedsRepo.getFeeds()
    }
    
}