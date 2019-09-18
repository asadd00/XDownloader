package com.android.downloadanything.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.downloadanything.R
import com.android.downloadanything.model.Feed

class FeedsAdapter(var context: Context, private var feedList: ArrayList<Feed>): RecyclerView.Adapter<FeedsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feed = feedList[position]

        holder.tv_name.text = feed.user.name
        holder.tv_likes.text = String.format(context.getString(R.string.likes), feed.likes)
    }

    override fun getItemCount(): Int {
        return feedList.size
    }

    fun setListData(feedList: ArrayList<Feed>){
        this.feedList = feedList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_preview: ImageView
        var iv_profileIcon: ImageView
        var tv_name: TextView
        var tv_likes: TextView
        var tv_visitSite: TextView

        init {
            iv_preview = itemView.findViewById(R.id.iv_preivew)
            iv_profileIcon = itemView.findViewById(R.id.iv_profileIcon)
            tv_name = itemView.findViewById(R.id.tv_name)
            tv_likes = itemView.findViewById(R.id.tv_likes)
            tv_visitSite = itemView.findViewById(R.id.tv_visitSite)
        }
    }
}