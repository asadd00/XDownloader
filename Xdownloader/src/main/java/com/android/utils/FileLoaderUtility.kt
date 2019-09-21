package com.android.utils

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.downloadx.R
import com.android.lib.FileLoader
import com.android.model.FileTypes
import com.android.model.FileModel
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ThreadFactory


class FileLoaderUtility(val context: Context) {
    private val tag = "ttt FileLoaderUtility"
    private var isNoteEnabled = false
    private var channelId = ""
    private var onDownloadResultListener: FileLoader.OnDownloadResultListener? = null
    private var noteBuilder: Notification.Builder? = null
    private var notificationManager: NotificationManager? = null

    internal class FileThreadFactory : ThreadFactory {
        override fun newThread(runnable: Runnable): Thread {
            return Thread(runnable).apply {
                name = "FileLoader Thread"
                priority = Process.THREAD_PRIORITY_BACKGROUND
            }
        }
    }

    fun downloadFileFromURL(fileModel: FileModel, channelId: String,
                            onDownloadResultListener: FileLoader.OnDownloadResultListener?): String? {
        this.channelId = channelId
        isNoteEnabled = true
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        return downloadFileFromURL(fileModel, onDownloadResultListener)
    }

    fun downloadFileFromURL(fileModel: FileModel,
                            onDownloadResultListener: FileLoader.OnDownloadResultListener?): String? {
        this.onDownloadResultListener = onDownloadResultListener
        val url = URL(fileModel.fileUrl)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        val responseCode = urlConnection.responseCode
        if(onDownloadResultListener!= null && (responseCode < 200 || responseCode >= 300)){
            onDownloadResultListener.onError("Connection error!", responseCode)
            return ""
        }

        val inputStream = BufferedInputStream(urlConnection.inputStream)

        return saveFile(inputStream, fileModel)
    }

    fun saveFile(file: File, fileModel: FileModel,
                 onDownloadResultListener: FileLoader.OnDownloadResultListener?): String{

        Log.d(tag, "path: " + context.getExternalFilesDir(null)?.path)

        val filetosave = File(context.getExternalFilesDir(null)?.path, fileModel.fileName)
        filetosave.parentFile.createNewFile()
        filetosave.createNewFile()

        val in_stream = file.inputStream()

        try {
            FileOutputStream(filetosave).use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (true) {
                    read = in_stream.read(buffer)
                    if(read == -1) break

                    output.write(buffer, 0, read)
                }

                output.flush()
                onDownloadResultListener?.onSuccess(filetosave.path)
            }
        } finally {
            in_stream.close()
        }


//        MediaScannerConnection.scanFile(context,
//            arrayOf(file.toString()), null
//        ) { path, uri ->
//            Log.i("ExternalStorage", "Scanned $path:")
//            Log.i("ExternalStorage", "-> uri=$uri")
//        }
        return file.absolutePath
    }

    fun saveFile(inputStream: BufferedInputStream, fileModel: FileModel): String{
        var ext = ".file"

        when {
            fileModel.fileType == FileTypes.TYPE_PDF -> ext = "pdf"
            fileModel.fileType == FileTypes.TYPE_DOC -> ext = "docx"
            fileModel.fileType == FileTypes.TYPE_IMAGE -> ext = "png"
            fileModel.fileType == FileTypes.TYPE_VIDEO -> ext = "mp4"
            fileModel.fileType == FileTypes.TYPE_AUDIO -> ext = "mp3"
        }

        val filetosave = File(context.getExternalFilesDir(null)?.path, "${fileModel.fileName}.$ext")
        Log.d(tag, "filepath: ${filetosave.path}")
        filetosave.parentFile.createNewFile()
        filetosave.createNewFile()

        if(isNoteEnabled){
            noteBuilder = buildNote()
            notificationManager?.notify(0, noteBuilder?.build())
        }

        try {
            FileOutputStream(filetosave).use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (true) {
                    read = inputStream.read(buffer)
                    if(read == -1) break

                    output.write(buffer, 0, read)
                }

                output.flush()
                onDownloadResultListener?.onSuccess(filetosave.path)
                if(isNoteEnabled) notificationManager?.cancel(0)
            }
        } finally {
            inputStream.close()
        }

        return filetosave.absolutePath
    }

    fun buildNote(): Notification.Builder{
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context)
        }

        builder.setContentTitle(context.getString(R.string.note_title))
        builder.setAutoCancel(false)
        builder.setSmallIcon(android.R.drawable.stat_sys_download)
        builder.setOngoing(true)

        return builder
    }
}