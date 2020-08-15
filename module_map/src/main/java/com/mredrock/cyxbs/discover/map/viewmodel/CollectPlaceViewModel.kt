package com.mredrock.cyxbs.discover.map.viewmodel

import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.discover.map.BuildConfig
import com.mredrock.cyxbs.discover.map.network.ApiService
import com.mredrock.cyxbs.discover.map.utils.Toast
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CollectPlaceViewModel : BaseViewModel() {

    init {
        ApiGenerator.registerNetSettings(2019212381, { builder -> retrofitConfig(builder) }, { builder -> okHttpConfig(builder) }, true)
    }

    fun retrofitConfig(builder: Retrofit.Builder): Retrofit.Builder {
        builder.baseUrl("https://cyxbsmobile.redrock.team/wxapi/magipoke-stumap/")
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
    fun addCollectPlace(placeId: Int) {
        ApiGenerator.getApiService(2019212381, ApiService::class.java)
                .addCollectPlace(placeId)
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    if (it.status == 200){
                        Toast.toast("收藏成功")
                    }
                    LogUtils.e("zt","收藏成功")
                }.lifeCycle()
    }
    fun deleteCollectPlace(placeId: Int){
        ApiGenerator.getApiService(2019212381, ApiService::class.java)
                .deleteCollectPlace(placeId)
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    if (it.status == 200)
                        Toast.toast("取消成功")
                    LogUtils.d("zt","取消收藏成功")
                }.lifeCycle()
    }
}