package com.android.downloadanything.model

import java.io.Serializable

class User(var id:String, var username:String, var name:String, var profile_image:ProfileImage, var links:Links) : Serializable
{
    inner class ProfileImage(var small:String, var medium:String, var large:String) : Serializable
}