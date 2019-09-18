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

class FeedsGridAdapter(var context: Context, private var feedList: ArrayList<Feed>): RecyclerView.Adapter<FeedsGridAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feed_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feed = feedList[position]

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
        var tv_likes: TextView

        init {
            iv_preview = itemView.findViewById(R.id.iv_preivew)
            tv_likes = itemView.findViewById(R.id.tv_likes)
        }
    }
}