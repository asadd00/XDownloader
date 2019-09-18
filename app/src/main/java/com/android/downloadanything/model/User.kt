package com.android.downloadanything.model

class User(var id:String, var username:String, var name:String, var profile_image:ProfileImage, var links:Links){


    inner class ProfileImage(var small:String, var medium:String, var large:String)
}