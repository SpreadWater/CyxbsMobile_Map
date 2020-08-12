package com.mredrock.cyxbs.discover.map.network

import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
interface ApiService
{
    @GET("http://118.31.20.31:8080/basic")
    fun getPlaceItemsList()
    @GET("http://118.31.20.31:8080/hot")
    fun getPlaceHot()
    @PATCH("http://118.31.20.31:8080/rockmap/addkeep ")
    fun addCollectPlace(@Field("place_nickname")place_nickname:String,@Field("place_id")place_id:Int)
    @GET("http://118.31.20.31:8080/rockmap/collect ")
    fun getCollectPlaceList()
    @POST("http://118.31.20.31:8080/searchtype")
    fun searchPlaceType(@Field("code")code:String)
    @POST("http://118.31.20.31:8080/detailsite")
    fun getPlaceDetail(@Field("place_id")place_id: Int)
}