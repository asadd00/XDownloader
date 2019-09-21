package com.android.downloadanything.view.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.downloadanything.R
import com.android.downloadanything.model.Feed
import com.android.main.Xdownloader
import androidx.recyclerview.widget.LinearLayoutManager




class FeedsAdapter(var context: Activity, var feedList: ArrayList<Feed>, private val recyclerView: RecyclerView): RecyclerView.Adapter<FeedsAdapter.ViewHolder>() {

    lateinit var onItemClickListener: OnItemClickListener
    lateinit var onLoadMoreListener: OnLoadMoreListener
    private val visibleThreshold = 10
    private var visibleItemCount = 0
    private var firstVisibleItemPosition: Int = 0
    private var totalItemCount: Int = 0
    private var loading: Boolean = false

    init {
        initLoadmoreThings()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feed = feedList[position]

        holder.tv_name.text = feed.user.name
        holder.tv_likes.text = String.format(context.getString(R.string.likes), feed.likes)
        Xdownloader.loadImage(context).load(holder.iv_preview, feed.urls.small)
        Xdownloader.loadImage(context).load(holder.iv_profileIcon, feed.user.profile_image.small)

        if(feed.links.html == "") holder.tv_visitSite.visibility = View.GONE
        else holder.tv_visitSite.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return feedList.size
    }

    fun setListData(feedList: ArrayList<Feed>){
        this.feedList.addAll(feedList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_preview: ImageView = itemView.findViewById(R.id.iv_preivew)
        var iv_profileIcon: ImageView = itemView.findViewById(R.id.iv_profileIcon)
        var tv_name: TextView = itemView.findViewById(R.id.tv_name)
        var tv_likes: TextView = itemView.findViewById(R.id.tv_likes)
        var tv_visitSite: TextView = itemView.findViewById(R.id.tv_visitSite)

        init {
            tv_visitSite.setOnClickListener { view ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(feedList[layoutPosition].links.html)
                context.startActivity(intent)
            }

            itemView.setOnClickListener{
                onItemClickListener.onItemClicked(layoutPosition)
            }

            iv_profileIcon.setOnClickListener{
                onItemClickListener.onUserImageClicked(layoutPosition)
            }
        }

    }

    fun setLoaded(){loading = false}

    private fun initLoadmoreThings(){
        if (recyclerView.layoutManager is LinearLayoutManager) {
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    visibleItemCount = linearLayoutManager.childCount
                    totalItemCount = linearLayoutManager.itemCount
                    firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

                    if (!loading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= visibleThreshold) {
                        // End has been reached
                        // Do something
                        onLoadMoreListener.onLoadMore()
                        loading = true
                    }
                }
            })
        }
    }

    interface OnItemClickListener{
        fun onItemClicked(position: Int)
        fun onUserImageClicked(position: Int)
    }

    interface OnLoadMoreListener{
        fun onLoadMore()
    }

}