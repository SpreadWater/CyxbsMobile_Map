package com.mredrock.cyxbs.discover.map.viewmodel

import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.discover.map.BuildConfig
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.network.ApiService
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 *@date 2020-8-7
 *@author zhangsan
 *@description
 */
class SearchViewModel:BaseViewModel() {
    init {
        ApiGenerator.registerNetSettings(2019212381, { builder -> retrofitConfig(builder) }, { builder -> okHttpConfig(builder) }, true)

    }

    fun retrofitConfig(builder: Retrofit.Builder): Retrofit.Builder {
        builder.baseUrl(PlaceData.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        return builder
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
    fun SearchPlace(placeid:Int){
        ApiGenerator.getApiService(2019212381,ApiService::class.java)
                .addhot(placeid)
                .setSchedulers()
                .safeSubscribeBy {
                    if (it.status==200){
                        LogUtils.d("zt","搜索成功!")
                    }
                }.lifeCycle()
    }
}