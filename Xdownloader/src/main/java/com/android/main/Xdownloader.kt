package com.android.main

import android.content.Context
import com.android.lib.FileLoader
import com.android.lib.ImageLoader

class Xdownloader{
    companion object{
        fun downloadFile(context: Context): FileLoader{
            return FileLoader(context).getInstance()
        }

        fun loadImage(context: Context): ImageLoader{
            return ImageLoader(context).getInstance()
        }
    }
}