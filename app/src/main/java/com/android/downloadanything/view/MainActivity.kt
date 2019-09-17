package com.android.downloadanything.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.android.FileLoader
import com.android.FileTypes
import com.android.downloadanything.R
import com.android.downloadanything.model.Feed
import com.android.downloadanything.remoteRepository.RetUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    val tag = "ttt MainActivity"
    lateinit var iv_preview: ImageView
    lateinit var b_getfile: Button

    lateinit var feedList: ArrayList<Feed>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    fun init(){
        askPermissions()
        iv_preview = findViewById(R.id.iv_preview)
        b_getfile = findViewById(R.id.b_getfile)

        b_getfile.setOnClickListener { view ->
            if(!hasRequiredPermissions()){
                askPermissions()
            }
            else
                FileLoader.with(baseContext)
                    .download("http://www.africau.edu/images/default/sample.pdf", FileTypes.TYPE_PDF)
        }

        feedList = ArrayList()
        makeGetDataRequest()
    }

    fun askPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    fun hasRequiredPermissions(): Boolean{
        return ContextCompat.checkSelfPermission(baseContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(baseContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun makeGetDataRequest(){
        val retApi = RetUtils.getAPIService()
        retApi.getRawData().enqueue(object: Callback<ArrayList<Feed>>{
            override fun onFailure(call: Call<ArrayList<Feed>>, t: Throwable)
            {
                Log.d(tag, "error!")
            }

            override fun onResponse(call: Call<ArrayList<Feed>>, response: Response<ArrayList<Feed>>)
            {
                if(response.isSuccessful){
                    feedList.addAll(response.body() as Collection<Feed>)
                    Log.d(tag, "len: ${feedList.size}")
                }
            }

        })
    }
}
