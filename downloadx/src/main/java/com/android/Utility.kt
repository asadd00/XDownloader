package com.android

import android.content.Context
import android.os.Process
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ThreadFactory


object Utility {
    private val tag = "ttt Utility"
    internal class FileThreadFactory : ThreadFactory {
        override fun newThread(runnable: Runnable): Thread {
            return Thread(runnable).apply {
                name = "FileLoader Thread"
                priority = Process.THREAD_PRIORITY_BACKGROUND
            }
        }
    }

    fun downloadFileFromURL(context: Context, fileModel: FileModel): String? {
        Log.d(tag, "downloadFileFromURL")
        val url = URL(fileModel.fileUrl)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        val responseCode =urlConnection.responseCode
        Log.d(tag, "responseCode: $responseCode")
        val inputStream = BufferedInputStream(urlConnection.inputStream)

        return saveFile(context, inputStream, fileModel)
    }

    fun saveFile(context: Context, file: File, fileModel: FileModel): String{
        Log.d(tag, "saveFile")
        Log.d(tag, "len: " + file.length())
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

    fun saveFile(context: Context, inputStream: BufferedInputStream, fileModel: FileModel): String{
        Log.d(tag, "saveFile")
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
            }
        } finally {
            inputStream.close()
        }

        return filetosave.absolutePath
    }
}