package com.android.downloadx.image_loader

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.util.LruCache
import android.widget.ImageView
import java.io.File
import java.util.*
import java.util.Collections.synchronizedMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MediaLoader (context: Context)  {
    private val tag = "ttt MediaLoader"
    private val maxCacheSize: Int = (Runtime.getRuntime().maxMemory() / 1024).toInt()/8
    private val memoryCache: LruCache<String, Bitmap>

    private val executorService: ExecutorService

    private val imageViewMap = synchronizedMap(WeakHashMap<ImageView, String>())
    private val handler: Handler

    init {
        memoryCache = object : LruCache<String, Bitmap>(maxCacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than number of items.
                val file = File("")
                return bitmap.byteCount / 1024
            }
        }

        executorService = Executors.newFixedThreadPool(5, Utility.ImageThreadFactory())
        handler = Handler()

        val metrics = context.resources.displayMetrics
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels


    }

    companion object {

        private var INSTANCE: MediaLoader? = null

        internal var screenWidth = 0
        internal var screenHeight = 0

        @Synchronized
        fun with(context: Context): MediaLoader {

            require(context != null) {
                "MediaLoader:with - Context should not be null."
            }

            return INSTANCE ?: MediaLoader(context).also {
                INSTANCE = it
            }

        }
    }

    fun load(imageView: ImageView, imageUrl: String) {

        require(imageView != null) {
            "$tag:load - ImageView should not be null."
        }

        require(imageUrl != null && imageUrl.isNotEmpty()) {
            "$tag:load - Image Url should not be empty"
        }

        imageView.setImageResource(0)
        imageViewMap[imageView] = imageUrl

        val bitmap = checkImageInCache(imageUrl)
        bitmap?.let {
            loadImageIntoImageView(imageView, it, imageUrl)
        } ?: run {
            executorService.submit(PhotosLoader(ImageRequest(imageUrl, imageView)))
        }
    }

    @Synchronized
    private  fun loadImageIntoImageView(imageView: ImageView, bitmap: Bitmap?, imageUrl: String) {

        require(bitmap != null) {
            "$tag:loadImageIntoImageView - Bitmap should not be null"
        }

        val scaledBitmap = Utility.scaleBitmapForLoad(bitmap, imageView.width, imageView.height)

        scaledBitmap?.let {
            if(!isImageViewReused(ImageRequest(imageUrl, imageView))) imageView.setImageBitmap(scaledBitmap)
        }
    }

    private fun isImageViewReused(imageRequest: ImageRequest): Boolean {
        val tag = imageViewMap[imageRequest.imageView]
        return tag == null || tag != imageRequest.imgUrl
    }

    @Synchronized
    private fun checkImageInCache(imageUrl: String): Bitmap? = memoryCache.get(imageUrl)

    inner class DisplayBitmap(private var imageRequest: ImageRequest) : Runnable {
        override fun run() {
            if(!isImageViewReused(imageRequest)) loadImageIntoImageView(imageRequest.imageView, checkImageInCache(imageRequest.imgUrl), imageRequest.imgUrl)
        }
    }

    inner class ImageRequest(var imgUrl: String, var imageView: ImageView)

    inner class PhotosLoader(private var imageRequest: ImageRequest) : Runnable {

        override fun run() {

            if(isImageViewReused(imageRequest)) return

            val bitmap = Utility.downloadBitmapFromURL(imageRequest.imgUrl)
            memoryCache.put(imageRequest.imgUrl, bitmap)

            if(isImageViewReused(imageRequest)) return

            val displayBitmap = DisplayBitmap(imageRequest)
            handler.post(displayBitmap)
        }
    }
}