package com.android.downloadanything.model

class Feed (id:String, created_at:String, width:Int, height:Int, color:String,
            likes:Int, liked_by_user:Boolean, user:User, currentUserCollections: CurrentUserCollections,
            urls: Urls, categories:ArrayList<Category>, links: Links){

    inner class Urls(raw:String, full:String, regular:String, small:String, thumb:String)

    inner class CurrentUserCollections()
}