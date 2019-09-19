package com.android.downloadanything.repository

import com.android.downloadanything.utils.Defaults
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class RetClient {
    companion object{
        var retrofit: Retrofit? = null

        fun getClient(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(Defaults.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    //.client(buildLoggingClient())
                    .build()
            }
            return retrofit!!
        }

        fun <T> buildService(type: Class<T>): T {
            return retrofit!!.create(type)
        }

        fun buildLoggingClient(): OkHttpClient{
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)
            return httpClient.build()
        }
    }


}