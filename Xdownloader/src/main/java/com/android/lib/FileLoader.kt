package com.android.lib

import android.content.Context
import android.util.Log
import android.util.LruCache
import com.android.model.FileModel
import com.android.model.FileTypes
import com.android.utils.FileLoaderUtility
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FileLoader internal constructor(private val context: Context)  {
    private val tag = "ttt FileLoader"
    private lateinit var fileType: FileTypes
    private var fileName = ""
    private var isCacheEnabled = false
    private var onDownloadResultListener: OnDownloadResultListener? = null
    private val executorService: ExecutorService
    private var maxCacheSize: Int = (Runtime.getRuntime().maxMemory() / 1024).toInt()/8
    private val memoryCache: LruCache<String, File>

    init {
        memoryCache = object : LruCache<String, File>(maxCacheSize) {
            override fun sizeOf(key: String, file: File): Int {
                return (file.length() / 1024).toInt()
            }
        }

        executorService = Executors.newFixedThreadPool(5, FileLoaderUtility.FileThreadFactory())
    }

    companion object {
        private var INSTANCE: FileLoader? = null
    }

    @Synchronized
    internal fun getInstance(): FileLoader{
//        return INSTANCE ?: FileLoader(
//            context
//        ).also {
//            INSTANCE = it
//        }

        INSTANCE = FileLoader(context)
        return INSTANCE!!
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
            FileLoaderUtility.saveFile(context, file,
                FileModel(fileUrl, fileType, fileName),
                onDownloadResultListener
            )
        } ?: run {
            executorService.submit(FileLoadingThread(
                FileModel(
                    fileUrl,
                    fileType,
                    fileName
                )
            ))
        }
    }

    @Synchronized
    private fun checkFileInCache(fileUrl: String): File? = memoryCache.get(fileUrl)

    internal inner class FileLoadingThread(private var fileModel: FileModel) : Runnable {

        override fun run() {
            Log.d(tag, "FileLoadingThread")
            val filepath = FileLoaderUtility.downloadFileFromURL(context, fileModel, onDownloadResultListener)

            if(!isCacheEnabled) return
            memoryCache.put(fileModel.fileUrl, File(filepath!!))

        }
    }

    interface OnDownloadResultListener{
        fun onSuccess(filePath: String)
        fun onError(error:String = "unknown error", errorCode:Int = 0)
    }

    fun setOnDownloadResultListener(onDownloadResultListener: OnDownloadResultListener): FileLoader{
        this.onDownloadResultListener = onDownloadResultListener
        return INSTANCE!!
    }
}