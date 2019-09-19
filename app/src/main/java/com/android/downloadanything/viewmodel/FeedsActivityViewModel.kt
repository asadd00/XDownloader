package com.android.downloadanything.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.downloadanything.model.Feed

class FeedsActivityViewModel: ViewModel() {
    private lateinit var mFeedList: MutableLiveData<ArrayList<Feed>>

    fun getFeeds(): LiveData<ArrayList<Feed>>{
        return mFeedList
    }
    
}