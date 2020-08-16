package com.mredrock.cyxbs.discover.map.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.map.bean.*
import com.mredrock.cyxbs.discover.map.bean.CollectPlace
import com.mredrock.cyxbs.discover.map.bean.PlaceBasicData
import io.reactivex.Observable
import io.reactivex.internal.operators.observable.ObservableError
import retrofit2.http.*
import java.io.File

interface ApiService {
    //地图基础数据
    @GET("basic")
    fun getPlaceItemsList(): Observable<RedrockApiWrapper<PlaceBasicData>>

    @GET("hot")
    fun getPlaceHot(): Observable<RedrockApiWrapper<String>>

    @GET("rockmap/collect")
    fun getCollectPlaceList(): Observable<RedrockApiWrapper<CollectPlace>>

    @POST("searchtype")
    @FormUrlEncoded
    fun searchPlaceType(@Field("code") code: String): Observable<RedrockApiWrapper<List<Int>>>

    @POST("detailsite")
    @FormUrlEncoded
    fun getPlaceDetail(@Field("place_id") place_id: Int): Observable<RedrockApiWrapper<PlaceDetail>>

    @Multipart
    @POST("rockmap/upload")
    fun uploadImage(@Part("file") file: File, @Part("place_id") place_id: Int):Observable<RedrockApiStatus>

    @POST("addhot")
    @FormUrlEncoded
    fun addhot(@Field("id") id: Int):Observable<RedrockApiStatus>

    @GET("button")
    fun button(): Observable<RedrockApiWrapper<TabLayoutTitles>>

    @PATCH("rockmap/addkeep")
    @FormUrlEncoded
    fun addCollectPlace(@Field("place_id") place_id: Int): Observable<RedrockApiStatus>

    @Multipart
    @HTTP(method = "DELETE", path = "rockmap/deletekeep", hasBody = true)
    fun deleteCollectPlace(@Part("place_id") place_id: Int): Observable<RedrockApiStatus>

}