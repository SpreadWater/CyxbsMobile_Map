package com.mredrock.cyxbs.discover.map.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.discover.map.BuildConfig
import com.mredrock.cyxbs.discover.map.bean.CollectPlace
import com.mredrock.cyxbs.discover.map.bean.PlaceDetail
import com.mredrock.cyxbs.discover.map.network.ApiService
import com.mredrock.cyxbs.discover.map.utils.Toast
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PlaceDetailViewModel : BaseViewModel() {
    val collectPlaces = MutableLiveData<CollectPlace>()
    val titles = listOf<String>("入校报到点", "运动场", "教学楼", "图书", "快递")
    var placeItemDetail = MutableLiveData<PlaceDetail>()

    init {
        ApiGenerator.registerNetSettings(2019212381, { builder -> retrofitConfig(builder) }, { builder -> okHttpConfig(builder) }, true)
    }

    fun retrofitConfig(builder: Retrofit.Builder): Retrofit.Builder {
        builder.baseUrl("https://cyxbsmobile.redrock.team/wxapi/magipoke-stumap/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        return builder
    }


    fun getCollectPlace() {
        ApiGenerator.getApiService(2019212381, ApiService::class.java)
                .getCollectPlaceList()
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    collectPlaces.value = it
                }.lifeCycle()
    }
    fun okHttpConfig(builder: okhttp3.OkHttpClient.Builder): okhttp3.OkHttpClient.Builder {
        builder.run {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
        }
        return builder
    }

    fun getPlaceDetail(placeId: Int) {
        ApiGenerator.getApiService(2019212381, ApiService::class.java)
                .getPlaceDetail(placeId)
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    if (it != null) {
                        placeItemDetail.value = it
                    }
                }
    }

}