package com.android.downloadanything.file_loader

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.util.LruCache
import android.widget.ImageView
import com.android.downloadanything.file_loader.FilesUtils1
import java.io.File
import java.util.*
import java.util.Collections.synchronizedMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FileLoader1 (val context: Context)  {

    private var maxCacheSize: Int = (Runtime.getRuntime().maxMemory() / 1024).toInt()/8
    private val memoryCache: LruCache<String, File>

    private val executorService: ExecutorService

    private val imageViewMap = synchronizedMap(WeakHashMap<String, String>())
    private val handler: Handler

    init {
        memoryCache = object : LruCache<String, File>(maxCacheSize) {
            override fun sizeOf(key: String, file: File): Int {
                // The cache size will be measured in kilobytes rather than number of items.
                //val file = File("")
                return (file.length() / 1024).toInt()
            }
        }

        executorService = Executors.newFixedThreadPool(5, FilesUtils1.ImageThreadFactory())
        handler = Handler()

        val metrics = context.resources.displayMetrics
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels


    }

    companion object {

        private var INSTANCE: FileLoader1? = null

        internal var screenWidth = 0
        internal var screenHeight = 0

        @Synchronized
        fun with(context: Context): FileLoader1 {

            require(context != null) {
                "ImageLoader:with - Context should not be null."
            }

            return INSTANCE
                ?: FileLoader1(context).also {
                INSTANCE = it
            }

        }
    }

    fun setCacheSize(cacheSizeInMB: Int): FileLoader1 {
        var cacheSize = cacheSizeInMB * 1024 * 1024

        if(cacheSize in 1 until maxCacheSize){
            maxCacheSize = cacheSize
        }

        return INSTANCE
            ?: FileLoader1(context).also {
                INSTANCE = it
            }

    }

    fun load(imageUrl: String) {
        
        require(imageUrl != null && imageUrl.isNotEmpty()) {
            "ImageLoader:load - Image Url should not be empty"
        }
        
        imageViewMap[imageUrl] = imageUrl

        val file = checkImageInCache(imageUrl)
        file?.let {
            FilesUtils1.saveFile(context, file)
        } ?: run {
            executorService.submit(PhotosLoader(ImageRequest(imageUrl)))
        }
    }

    @Synchronized
    private  fun loadImageIntoImageView(file: File?, imageUrl: String) {

        require(file != null) {
            "ImageLoader:loadImageIntoImageView - Bitmap should not be null"
        }

        val filepath = FilesUtils1.saveFile(context, file)
    }

    private fun isImageViewReused(imageRequest: ImageRequest): Boolean {
        val tag = imageViewMap[imageRequest.imgUrl]
        return tag == null || tag != imageRequest.imgUrl
    }

    @Synchronized
    private fun checkImageInCache(imageUrl: String): File? = memoryCache.get(imageUrl)

//    inner class DisplayBitmap(private var imageRequest: ImageRequest) : Runnable {
//        override fun run() {
//            if(!isImageViewReused(imageRequest)) loadImageIntoImageView(imageRequest.imageView, checkImageInCache(imageRequest.imgUrl), imageRequest.imgUrl)
//        }
//    }

    inner class ImageRequest(var imgUrl: String)

    inner class PhotosLoader(private var imageRequest: ImageRequest) : Runnable {

        override fun run() {

            //if(isImageViewReused(imageRequest)) return

            val filepath = FilesUtils1.downloadBitmapFromURL(context, imageRequest.imgUrl)
            memoryCache.put(imageRequest.imgUrl, File(filepath!!))

            //if(isImageViewReused(imageRequest)) return

//            val displayBitmap = DisplayBitmap(imageRequest)
//            handler.post(displayBitmap)
        }
    }
}