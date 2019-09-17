package com.android

import android.content.Context
import android.util.Log
import android.util.LruCache
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FileLoader (val context: Context)  {
    private val tag = "ttt FileLoader"
    private lateinit var fileType: FileTypes
    private var fileName = ""
    private var isCacheEnabled = false
    private val executorService: ExecutorService
    private var maxCacheSize: Int = (Runtime.getRuntime().maxMemory() / 1024).toInt()/8
    private val memoryCache: LruCache<String, File>

    init {
        memoryCache = object : LruCache<String, File>(maxCacheSize) {
            override fun sizeOf(key: String, file: File): Int {
                return (file.length() / 1024).toInt()
            }
        }

        executorService = Executors.newFixedThreadPool(5, Utility.FileThreadFactory())
    }

    companion object {
        private var INSTANCE: FileLoader? = null

        @Synchronized
        fun with(context: Context): FileLoader {

            require(context != null) {
                "FileLoader:with - Context should not be null."
            }

            return INSTANCE
                ?: FileLoader(context).also {
                INSTANCE = it
            }
        }
    }

    fun setCacheSize(cacheSizeInMB: Int): FileLoader {
        var cacheSize = cacheSizeInMB * 1024 * 1024

        if(cacheSize in 1 until maxCacheSize){
            maxCacheSize = cacheSize
        }

        return INSTANCE!!
    }

    fun setFileName(fileName: String): FileLoader {
        this.fileName = fileName

        return INSTANCE!!
    }

    fun setCacheEnabled(isCacheEnabled: Boolean): FileLoader {
        this.isCacheEnabled = isCacheEnabled

        return INSTANCE!!
    }

    fun download(fileUrl: String, fileType: FileTypes) {
        this.fileType = fileType
        Log.d(tag, "download")
        require(fileUrl.isNotEmpty()) {
            "FileLoader:load - Url should not be empty"
        }

        if(fileName.isEmpty()) fileName = System.currentTimeMillis().toString()

        val file = checkFileInCache(fileUrl)
        file?.let {
            Utility.saveFile(context, file, FileModel(fileUrl, fileType, fileName))
        } ?: run {
            executorService.submit(FileLoadingThread(FileModel(fileUrl, fileType, fileName)))
        }
    }

    @Synchronized
    private fun checkFileInCache(fileUrl: String): File? = memoryCache.get(fileUrl)

    inner class FileLoadingThread(private var fileModel: FileModel) : Runnable {

        override fun run() {
            Log.d(tag, "FileLoadingThread")
            val filepath = Utility.downloadFileFromURL(context, fileModel)

            if(!isCacheEnabled) return
            memoryCache.put(fileModel.fileUrl, File(filepath!!))

        }
    }
}