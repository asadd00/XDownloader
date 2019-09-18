package com.android.lib

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import com.android.downloadx.R
import com.android.model.ImageModel
import com.android.utils.ImageLoaderUtility
import java.util.*
import java.util.Collections.synchronizedMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageLoader internal constructor(private val context: Context)  {
    private val tag = "ttt ImageLoader"

    private val imageViewMap = synchronizedMap(WeakHashMap<ImageView, String>())
    private val handler: Handler
    private var placeholderResId = R.drawable.image_placeholder
    private var isCacheEnabled = false
    private val executorService: ExecutorService
    private val memoryCache: LruCache<String, Bitmap>
    private var maxCacheSize: Int = (Runtime.getRuntime().maxMemory() / 1024).toInt()/8

    init {
        memoryCache = object : LruCache<String, Bitmap>(maxCacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }

        executorService = Executors.newFixedThreadPool(5, ImageLoaderUtility.ImageThreadFactory())
        handler = Handler()

        val metrics = context.resources.displayMetrics
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
    }

    companion object {
        private var INSTANCE: ImageLoader? = null
        internal var screenWidth = 0
        internal var screenHeight = 0
    }

    @Synchronized
    internal fun getInstance(): ImageLoader{
        return INSTANCE ?: ImageLoader(
            context
        ).also {
            INSTANCE = it
        }
    }

    fun load(imageView: ImageView, imageUrl: String) {
        require(imageUrl.isNotEmpty()) {
            "ImageLoader:load - Image Url should not be empty"
        }

        imageView.setImageResource(placeholderResId)
        imageViewMap[imageView] = imageUrl

        val bitmap = checkImageInCache(imageUrl)
        bitmap?.let {
            Log.d(tag, "cached")
            loadImageIntoImageView(imageView, it, imageUrl)
        } ?: run {
            executorService.submit(ImageLoadingThread(ImageModel(imageUrl, imageView)))
        }
    }

    fun setCacheSize(cacheSizeInMB: Int): ImageLoader {
        var cacheSize = cacheSizeInMB * 1024 * 1024

        if(cacheSize in 1 until maxCacheSize){
            maxCacheSize = cacheSize
        }

        return INSTANCE!!
    }

    fun setCacheEnabled(isCacheEnabled: Boolean): ImageLoader {
        this.isCacheEnabled = isCacheEnabled

        return INSTANCE!!
    }

    fun placeholder(placeholderResId: Int): ImageLoader {

        require(placeholderResId != 0) {
            "ImageLoader:placeholder - placeholder should not be null."
        }

        this.placeholderResId = placeholderResId

        return INSTANCE
            ?: ImageLoader(context).also {
            INSTANCE = it
        }
    }

    @Synchronized
    private  fun loadImageIntoImageView(imageView: ImageView, bitmap: Bitmap?, imageUrl: String) {

        require(bitmap != null) {
            "ImageLoader:loadImageIntoImageView - Bitmap should not be null"
        }

        val scaledBitmap = ImageLoaderUtility.scaleBitmapForLoad(bitmap, imageView.width, imageView.height)

        scaledBitmap?.let {
            if(!isImageViewReused(ImageModel(imageUrl, imageView))) imageView.setImageBitmap(scaledBitmap)
        }
    }

    private fun isImageViewReused(imageModel: ImageModel): Boolean {
        val tag = imageViewMap[imageModel.imageView]
        return tag == null || tag != imageModel.imgUrl
    }

    @Synchronized
    private fun checkImageInCache(imageUrl: String): Bitmap? = memoryCache.get(imageUrl)

    internal inner class DisplayBitmap(private var imageRequest: ImageModel) : Runnable {
        override fun run() {
            if(!isImageViewReused(imageRequest)) loadImageIntoImageView(imageRequest.imageView, checkImageInCache(imageRequest.imgUrl), imageRequest.imgUrl)
        }
    }

    internal inner class ImageLoadingThread(private var imageModel: ImageModel) : Runnable {
        override fun run() {
            if(isImageViewReused(imageModel)) return

            val bitmap = ImageLoaderUtility.downloadBitmapFromURL(imageModel.imgUrl)
           // if(isCacheEnabled)
                memoryCache.put(imageModel.imgUrl, bitmap)

            if(isImageViewReused(imageModel)) return

            val displayBitmap = DisplayBitmap(imageModel)
            handler.post(displayBitmap)
        }
    }
}