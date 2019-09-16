package com.android.downloadanything.file_loader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.Process
import android.util.Log
import com.android.downloadanything.image_loader.ImageLoader
import java.io.*
import java.net.URL
import java.util.concurrent.ThreadFactory
import android.media.MediaScannerConnection
import android.net.Uri


object FilesUtils1 {

    // Thread Factory to set Thread priority to Background
    internal class ImageThreadFactory : ThreadFactory {
        override fun newThread(runnable: Runnable): Thread {
            return Thread(runnable).apply {
                name = "ImageLoader Thread"
                priority = Process.THREAD_PRIORITY_BACKGROUND
            }
        }
    }

    fun downloadBitmapFromURL(context: Context, imageUrl: String): String? {
        val url = URL(imageUrl)
        val inputStream = BufferedInputStream(url.openConnection().getInputStream())

        try {
            val file = File(context.cacheDir, "cacheFileAppeal.srl")
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (true) {
                    read = inputStream.read(buffer)
                    if(read == -1) break;

                    output.write(buffer, 0, read)
                }

                output.flush()
                return saveFile(context, file)
            }
        } finally {
            inputStream.close()
        }
    }

    fun scaleBitmapForLoad(bitmap: Bitmap, width: Int, height: Int): Bitmap? {

        if(width == 0 || height == 0) return bitmap

        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, 100, stream)
        val inputStream = BufferedInputStream(ByteArrayInputStream(stream.toByteArray()))

        // Scale Bitmap to required ImageView Size
        return scaleBitmap(inputStream, width, height)
    }

    private fun scaleBitmap(inputStream: BufferedInputStream, width: Int, height: Int) : Bitmap? {
        return BitmapFactory.Options().run {
            inputStream.mark(inputStream.available())

            inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, this)

            inSampleSize =
                calculateInSampleSize(this, width, height)

            inJustDecodeBounds = false
            inputStream.reset()
            BitmapFactory.decodeStream(inputStream, null,  this)
        }
    }

    // From Developer Site
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) inSampleSize *= 2
        }

        return inSampleSize
    }

    fun saveFile(context: Context, file: File): String{
        Log.d("ttt FileUtils1", "saveFile")
        Log.d("ttt FileUtils1", "len: " + file.length())
        Log.d("ttt FileUtils1", "path: " + context.getExternalFilesDir(null)?.path)

        val filetosave = File(context.getExternalFilesDir(null)?.path, "mypdf.pdf")
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
}