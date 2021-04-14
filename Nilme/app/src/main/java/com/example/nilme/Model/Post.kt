package com.example.nilme.Model

class Post {

    private var postid: String = ""
    private var publisher: String = ""
    private var description: String = ""
    private var postimage: String = ""

    constructor()
    constructor(postid: String, publisher: String, description: String, postimage: String) {
        this.postid = postid
        this.publisher = publisher
        this.description = description
        this.postimage = postimage
    }


    fun getPostid():String{
        return postid
    }

    fun setPostid(postid: String) {
        this.postid = postid
    }

    fun getPublisher():String{
        return publisher
    }

    fun setPublisher(publisher: String) {
        this.publisher = publisher
    }

    fun getDescription():String{
        return description
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun getPostImage():String{
        return postimage
    }

    fun setPostImage(postimage: String) {
        this.postimage = postimage
    }

}