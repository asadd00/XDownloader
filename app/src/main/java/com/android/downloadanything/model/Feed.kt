package com.android.downloadanything.model

class Feed(
    var id: String,
    var created_at: String,
    var width: Int,
    var height: Int,
    var color: String,
    var likes: Int,
    var liked_by_user: Boolean,
    var user: User,
    var currentUserCollections: CurrentUserCollections,
    var urls: Urls,
    var categories: ArrayList<Category>,
    var links: Links
) {

    inner class Urls(var raw:String, var full:String, var regular:String, var small:String, var thumb:String)

    inner class CurrentUserCollections()
}