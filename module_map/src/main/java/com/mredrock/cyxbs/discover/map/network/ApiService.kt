package com.mredrock.cyxbs.discover.map.network

import androidx.room.Delete
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.map.bean.PlaceBasicData
import io.reactivex.Observable
import retrofit2.http.*

interface ApiService
{
    //地图基础数据
    @GET("basic")
    fun getPlaceItemsList():Observable<RedrockApiWrapper<PlaceBasicData>>
    @GET("hot")
    fun getPlaceHot()
    @PATCH("rockmap/addkeep")
    @FormUrlEncoded
    fun addCollectPlace(@Field("place_nickname")place_nickname:String,@Field("place_id")place_id:Int)
    @DELETE("rockmap/deletekeep")
    @FormUrlEncoded
    fun deleteCollectPlace(@Field("place_id")place_id: Int)
    @GET("rockmap/collect")
    @FormUrlEncoded
    fun getCollectPlaceList()
    @POST("searchtype")
    @FormUrlEncoded
    fun searchPlaceType(@Field("code")code:String)
    @POST("detailsite")
    @FormUrlEncoded
    fun getPlaceDetail(@Field("place_id")place_id: Int)
    @POST("rockmap/upload")
    @FormUrlEncoded
    fun uploadImage(@Field("file")file:String,@Field("place_id")place_id: Int)
    @POST("addhot")
    @FormUrlEncoded
    fun addhot(@Field("id")id:Int)
    @GET("button")
    fun button()
}