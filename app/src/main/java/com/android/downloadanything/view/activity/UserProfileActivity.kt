package com.android.downloadanything.view.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.downloadanything.R
import com.android.downloadanything.model.User
import com.android.downloadanything.utils.RecyclerViewItemDecorator
import com.android.downloadanything.view.adapter.UserGalleryAdapter
import com.android.main.Xdownloader

class UserProfileActivity : AppCompatActivity() {
    lateinit var rv_list: RecyclerView
    lateinit var iv_thumbnail: ImageView
    lateinit var tv_name: TextView
    lateinit var tv_email: TextView
    lateinit var tv_visitSite: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        init()
    }

    private fun init(){
        rv_list = findViewById(R.id.rv_list)
        iv_thumbnail = findViewById(R.id.iv_thumbnail)
        tv_name = findViewById(R.id.tv_name)
        tv_email = findViewById(R.id.tv_email)
        tv_visitSite = findViewById(R.id.tv_visitSite)

        if(intent != null && intent.hasExtra(User::class.java.simpleName)){
            val user = intent.getSerializableExtra(User::class.java.simpleName) as User
            tv_name.text = user.name
            tv_email.text = String.format("@%s",user.username)
            Xdownloader.loadImage(baseContext)
                .placeholder(R.drawable.profile_placeholder)
                .load(iv_thumbnail, user.profile_image.medium)

            tv_visitSite.setOnClickListener { view ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(user.links.html)
                startActivity(intent)
            }
        }

        val adapter = UserGalleryAdapter()
        val layoutManager = GridLayoutManager(baseContext, resources.getInteger(R.integer.grid_span))
        val decorator = RecyclerViewItemDecorator(8, RecyclerViewItemDecorator.GRID)
        rv_list.layoutManager = layoutManager
        rv_list.adapter = adapter
        rv_list.addItemDecoration(decorator)
    }
}
