package com.android.downloadanything.remoteRepository

import com.android.downloadanything.model.Feed
import com.android.downloadanything.utils.Defaults
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Multipart



interface RetAPI {
    @GET(Defaults.GET_RAW_DATA)
    fun getRawData(): Call<ArrayList<Feed>>
}