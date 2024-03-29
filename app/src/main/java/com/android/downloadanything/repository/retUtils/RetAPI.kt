package com.android.downloadanything.repository.retUtils

import com.android.downloadanything.model.Feed
import com.android.downloadanything.utils.Defaults
import retrofit2.Call
import retrofit2.http.GET


interface RetAPI {
    @GET(Defaults.Urls.GET_RAW_DATA)
    fun getRawData(): Call<ArrayList<Feed>>
}