package com.android.downloadanything.view.activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.android.downloadanything.R
import com.android.downloadanything.model.Feed
import com.android.downloadanything.model.User
import com.android.downloadanything.utils.Methods
import com.android.downloadanything.view.adapter.FeedsAdapter
import com.android.downloadanything.view.fragment.FeedsGridViewFragment
import com.android.downloadanything.view.fragment.ImagePreviewFragment
import com.android.downloadanything.viewmodel.FeedsActivityViewModel

class FeedsActivity : AppCompatActivity(), FeedsAdapter.OnItemClickListener {
    val tag = "ttt FeedsActivity"

    lateinit var adapter: FeedsAdapter
    lateinit var rv_list: RecyclerView
    lateinit var feedsActivityViewModel: FeedsActivityViewModel
    private lateinit var loader: Dialog
    lateinit var fm: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feed_style_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.m_grid -> showGridView(true)
            R.id.m_list -> showGridView(false)
        }

        return false
    }

    private fun init(){
        loader = Methods.getProgressLoader(this@FeedsActivity)
        fm = supportFragmentManager
        rv_list = findViewById(R.id.rv_list)

        initViewModel()

        adapter = FeedsAdapter(this@FeedsActivity, ArrayList<Feed>())
        adapter.onItemClickListener = this@FeedsActivity
        rv_list.adapter = adapter
    }

    private fun initViewModel(){
        feedsActivityViewModel = ViewModelProviders.of(this).get(FeedsActivityViewModel::class.java)
        feedsActivityViewModel.init()
        loader.show()
        feedsActivityViewModel.getFeeds()?.observe(this, Observer<ArrayList<Feed>> {
            if(loader.isShowing) loader.dismiss()
            if(it != null)
                adapter.setListData(it)
            adapter.notifyDataSetChanged()
        })
    }

    override fun onItemClicked(position: Int) {
        val feed = adapter.feedList[position]

        val imagePreviewFragment = ImagePreviewFragment()
        val bundle = Bundle()
        bundle.putString(ImagePreviewFragment.IMAGE_URL, feed.urls.regular)
        bundle.putString(ImagePreviewFragment.PROFILE_IMAGE, feed.user.profile_image.small)
        bundle.putString(ImagePreviewFragment.NAME, feed.user.name)
        bundle.putSerializable(ImagePreviewFragment.CATEGORY, feed.categories)

        imagePreviewFragment.arguments = bundle

        setFragment(imagePreviewFragment)
    }

    private fun setFragment(fragment: Fragment){
        fm.beginTransaction()
            .replace(R.id.container, fragment, fragment::class.java.simpleName)
            .commit()

        (findViewById<FrameLayout>(R.id.container)).visibility = View.VISIBLE
    }

    private fun removeFragment(tag:String){
        val fragment = fm.findFragmentByTag(tag)
        if(fragment != null){
            fm.beginTransaction()
                .remove(fragment)
                .commit()

            (findViewById<FrameLayout>(R.id.container)).visibility = View.GONE
        }
    }

    override fun onUserImageClicked(position: Int) {
        val intent = Intent(baseContext, UserProfileActivity::class.java)
        intent.putExtra(User::class.java.simpleName, adapter.feedList[position].user)
        startActivity(intent)
    }

    override fun onBackPressed() {
        val imagePreviewFragment = fm.findFragmentByTag(ImagePreviewFragment::class.java.simpleName)
        val gridViewFragment = fm.findFragmentByTag(FeedsGridViewFragment::class.java.simpleName)

        if(imagePreviewFragment != null){
            fm.beginTransaction().remove(imagePreviewFragment).commit()
            (findViewById<FrameLayout>(R.id.container)).visibility = View.GONE
        }
        else if(gridViewFragment != null){
            fm.beginTransaction().remove(gridViewFragment).commit()
            (findViewById<FrameLayout>(R.id.container)).visibility = View.GONE
        }
        else super.onBackPressed()
    }

    private fun showGridView(show: Boolean){
        if(show){
            val feedsGridViewFragment = FeedsGridViewFragment()
            val bundle = Bundle()
            bundle.putSerializable("feedList", adapter.feedList)
            feedsGridViewFragment.arguments = bundle
            setFragment(feedsGridViewFragment)
        }
        else{
            val fragment = fm.findFragmentByTag(FeedsGridViewFragment::class.java.simpleName)
            if(fragment != null)
                removeFragment(fragment::class.java.simpleName)
        }
    }
}
