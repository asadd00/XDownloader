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

class FeedsAdapter(var context: Activity, private var feedList: ArrayList<Feed>): RecyclerView.Adapter<FeedsAdapter.ViewHolder>() {

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
        this.feedList = feedList
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
        }
    }
}