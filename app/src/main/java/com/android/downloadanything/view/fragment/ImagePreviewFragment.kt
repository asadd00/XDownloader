package com.android.downloadanything.view.fragment


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.android.downloadanything.R
import com.android.downloadanything.model.Category
import com.android.lib.FileLoader
import com.android.main.Xdownloader
import com.android.model.FileTypes
import java.lang.Exception

class ImagePreviewFragment : Fragment() {
    companion object{
        const val IMAGE_URL = "imageUrl"
        const val PROFILE_IMAGE = "profileImage"
        const val NAME = "name"
        const val CATEGORY = "category"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View){
        if(arguments != null){
            val iv_preview = view.findViewById<ImageView>(R.id.iv_preivew)
            val iv_profileIcon = view.findViewById<ImageView>(R.id.iv_profileIcon)
            val tv_name = view.findViewById<TextView>(R.id.tv_name)
            val tv_categories = view.findViewById<TextView>(R.id.tv_categories)
            val iv_download = view.findViewById<ImageView>(R.id.iv_download)

            try {
                val imageUrl = arguments?.getString(IMAGE_URL)
                val profileImage= arguments?.getString(PROFILE_IMAGE)
                val name= arguments?.getString(NAME)
                val category: ArrayList<Category> = arguments?.getSerializable(CATEGORY) as ArrayList<Category>

                Xdownloader.loadImage(context!!).load(iv_preview, imageUrl!!)
                Xdownloader.loadImage(context!!).load(iv_profileIcon, profileImage!!)
                tv_name.text = name

                val categoryStr = StringBuilder()
                for (cat in category){
                    categoryStr.append(cat.title).append(", ")
                }
                tv_categories.text = String.format(getString(R.string.genres), categoryStr.toString())

                iv_download.setOnClickListener {
                    if(!hasRequiredPermissions()) {
                        askPermissions()
                        return@setOnClickListener
                    }

                    Xdownloader.downloadFile(context!!)
                        .setFileName("$name-${System.currentTimeMillis()}")
                        .setOnDownloadResultListener(object : FileLoader.OnDownloadResultListener {
                            override fun onSuccess(filePath: String) {
                                activity?.runOnUiThread{
                                    Toast.makeText(context, String.format("File downloaded at: %s", filePath), Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onError(error: String, errorCode:Int) {
                                activity?.runOnUiThread{
                                    Toast.makeText(context, String.format("File downloading error. ERROR_CODE_%s", errorCode), Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                        .download(imageUrl, FileTypes.TYPE_IMAGE)
                }

            }
            catch (e:Exception){e.printStackTrace()}
        }
    }

    fun askPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    fun hasRequiredPermissions(): Boolean{
        return ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

}
