package com.android.downloadanything.model

class User(id:String, username:String, name:String, profile_image:ProfileImage, links:Links){


    inner class ProfileImage(small:String, medium:String, large:String)
}