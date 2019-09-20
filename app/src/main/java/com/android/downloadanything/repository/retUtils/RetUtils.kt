package com.android.downloadanything.repository.retUtils

class RetUtils private constructor() {

    companion object{
        fun getAPIService(): RetAPI {
            return RetClient.getClient()
                .create(RetAPI::class.java)
        }
    }

}