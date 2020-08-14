package com.mredrock.cyxbs.discover.map.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.map.bean.*
import com.mredrock.cyxbs.discover.map.bean.CollectPlace
import com.mredrock.cyxbs.discover.map.bean.PlaceBasicData
import io.reactivex.Observable
import io.reactivex.internal.operators.observable.ObservableError
import retrofit2.http.*

interface ApiService
{
    //地图基础数据
    @GET("basic")
    fun getPlaceItemsList():Observable<RedrockApiWrapper<PlaceBasicData>>
    @GET("hot")
    fun getPlaceHot():Observable<RedrockApiWrapper<String>>
    @PATCH("rockmap/addkeep")
    @FormUrlEncoded
    fun addCollectPlace(@Field("place_nickname")place_nickname:String,@Field("place_id")place_id:Int):Observable<RedrockApiStatus>
    @DELETE("rockmap/deletekeep")
    @FormUrlEncoded
    fun deleteCollectPlace(@Field("place_id")place_id: Int)
    @GET("rockmap/collect")
    fun getCollectPlaceList():Observable<RedrockApiWrapper<List<CollectPlace>>>
    @POST("searchtype")
    @FormUrlEncoded
    fun searchPlaceType(@Field("code")code:String):Observable<RedrockApiWrapper<List<Int>>>
    @POST("detailsite")
    @FormUrlEncoded
    fun getPlaceDetail(@Field("place_id")place_id: Int):Observable<RedrockApiWrapper<PlaceDetail>>
    @POST("rockmap/upload")
    @FormUrlEncoded
    fun uploadImage(@Field("file")file:String,@Field("place_id")place_id: Int)
    @POST("addhot")
    @FormUrlEncoded
    fun addhot(@Field("id")id:Int)
    @GET("button")
    fun button()
}