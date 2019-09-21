package com.android.downloadanything.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.downloadanything.model.Feed
import com.android.downloadanything.repository.FeedsRepository

class FeedsActivityViewModel: ViewModel() {
    private val tag = "ttt FeedsActivityViewM"
    var mFeedList: MutableLiveData<ArrayList<Feed>>? = null
            private set

    private lateinit var feedsRepo: FeedsRepository

    fun init(){
        if(mFeedList != null) return

        feedsRepo = FeedsRepository()
        mFeedList = feedsRepo.getLiveDataInstance()
    }

    fun getFeeds(): LiveData<ArrayList<Feed>>?{
        //mFeedList = feedsRepo.getFeeds()
        return mFeedList
    }


    /**
     * dummy for pagination testing
     * data should be called from repository
     */
    fun getMoreFeeds(){
//        val list = mFeedList?.value
//        mFeedList?.value = list
        mFeedList = feedsRepo.getFeeds()
    }


}