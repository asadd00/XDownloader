package com.android.downloadanything.repository

import com.android.downloadanything.model.Feed
import com.android.downloadanything.utils.Defaults
import retrofit2.Call
import retrofit2.http.GET


interface RetAPI {
    @GET(Defaults.GET_RAW_DATA)
    fun getRawData(): Call<ArrayList<Feed>>
}