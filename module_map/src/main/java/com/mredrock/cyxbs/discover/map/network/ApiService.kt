package com.mredrock.cyxbs.discover.map.network

import androidx.room.Delete
import retrofit2.http.*

interface ApiService
{
    @GET("http://118.31.20.31:8080/basic")
    fun getPlaceItemsList()
    @GET("http://118.31.20.31:8080/hot")
    fun getPlaceHot()
    @PATCH("http://118.31.20.31:8080/rockmap/addkeep")
    fun addCollectPlace(@Field("place_nickname")place_nickname:String,@Field("place_id")place_id:Int)
    @DELETE("http://118.31.20.31:8080/rockmap/deletekeep")
    fun deleteCollectPlace(@Field("place_id")place_id: Int)
    @GET("http://118.31.20.31:8080/rockmap/collect")
    fun getCollectPlaceList()
    @POST("http://118.31.20.31:8080/searchtype")
    fun searchPlaceType(@Field("code")code:String)
    @POST("http://118.31.20.31:8080/detailsite")
    fun getPlaceDetail(@Field("place_id")place_id: Int)
    @POST("http://118.31.20.31:8080/rockmap/upload")
    fun uploadImage(@Field("file")file:String,@Field("place_id")place_id: Int)
    @POST("http://118.31.20.31:8080/addhot")
    fun addhot(@Field("id")id:Int)
    @GET("http://118.31.20.31:8080/button")
    fun button()
}