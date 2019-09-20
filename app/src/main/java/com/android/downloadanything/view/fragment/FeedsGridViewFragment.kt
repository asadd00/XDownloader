package com.android.downloadanything.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager

import com.android.downloadanything.R
import com.android.downloadanything.model.Feed
import com.android.downloadanything.utils.RecyclerViewItemDecorator
import com.android.downloadanything.view.adapter.FeedsGridAdapter
import kotlinx.android.synthetic.main.activity_main.*

class FeedsGridViewFragment : Fragment() {
    lateinit var feedList: ArrayList<Feed>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feeds_grid_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View){
        if(arguments != null) {
            feedList = arguments?.getSerializable("feedList") as ArrayList<Feed>
            val feedsGridAdapter = FeedsGridAdapter(context!!, feedList)
            rv_list.layoutManager = GridLayoutManager(context, resources.getInteger(R.integer.grid_span))
            rv_list.adapter = feedsGridAdapter
            rv_list.addItemDecoration(RecyclerViewItemDecorator(8, RecyclerViewItemDecorator.GRID))
        }
    }
}
