package com.android.downloadanything.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.android.downloadanything.R
import com.android.downloadanything.model.Feed
import com.android.downloadanything.model.User
import com.android.downloadanything.repository.retUtils.RetUtils
import com.android.downloadanything.view.adapter.FeedsAdapter
import com.android.downloadanything.view.fragment.ImagePreviewFragment
import com.android.downloadanything.viewmodel.FeedsActivityViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedsActivity : AppCompatActivity(), FeedsAdapter.OnItemClickListener {
    val tag = "ttt FeedsActivity"

    lateinit var feedList: ArrayList<Feed>
    lateinit var adapter: FeedsAdapter
    lateinit var rv_list: RecyclerView
    lateinit var feedsActivityViewModel: FeedsActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init(){
        feedList = ArrayList()
        rv_list = findViewById(R.id.rv_list)

        initViewModel()

        adapter = FeedsAdapter(this@FeedsActivity, feedsActivityViewModel.getFeeds().value!!)
        adapter.onItemClickListener = this@FeedsActivity
        rv_list.adapter = adapter

        feedsActivityViewModel.getFeedsFromRepo()
        //makeGetDataRequest()
    }

    private fun initViewModel(){
        feedsActivityViewModel = ViewModelProviders.of(this).get(FeedsActivityViewModel::class.java)
        feedsActivityViewModel.init()
        feedsActivityViewModel.getFeeds().observe(this, Observer<ArrayList<Feed>> {
            Log.d(tag, "onChanged")
            adapter.notifyDataSetChanged()
        })
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

    override fun onItemClicked(position: Int) {
        val feed = feedsActivityViewModel.getFeeds().value!![position]

        val imagePreviewFragment = ImagePreviewFragment()
        val bundle = Bundle()
        bundle.putString(ImagePreviewFragment.IMAGE_URL, feed.urls.regular)
        bundle.putString(ImagePreviewFragment.PROFILE_IMAGE, feed.user.profile_image.small)
        bundle.putString(ImagePreviewFragment.NAME, feed.user.name)
        bundle.putSerializable(ImagePreviewFragment.CATEGORY, feed.categories)

        imagePreviewFragment.arguments = bundle

        val fm = supportFragmentManager
        fm.beginTransaction()
            .replace(R.id.container, imagePreviewFragment, ImagePreviewFragment::class.java.simpleName)
            .commit()

        (findViewById<FrameLayout>(R.id.container)).visibility = View.VISIBLE
    }

    override fun onUserImageClicked(position: Int) {
        val intent = Intent(baseContext, UserProfileActivity::class.java)
        intent.putExtra(User::class.java.simpleName, feedList[position].user)
        startActivity(intent)
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
                    adapter.setListData(feedList)
                }
            }

        })
    }

    override fun onBackPressed() {
        val fm = supportFragmentManager
        val imagePreviewFragment = fm.findFragmentByTag(ImagePreviewFragment::class.java.simpleName)
        if(imagePreviewFragment != null){
            fm.beginTransaction().remove(imagePreviewFragment).commit()
            (findViewById<FrameLayout>(R.id.container)).visibility = View.GONE
        }
        else super.onBackPressed()
    }
}
