package com.mredrock.cyxbs.discover.map.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.discover.map.BuildConfig
import com.mredrock.cyxbs.discover.map.bean.PlaceBasicData
import com.mredrock.cyxbs.discover.map.model.dao.HistoryPlaceDao
import com.mredrock.cyxbs.discover.map.network.ApiService
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MapViewModel : BaseViewModel() {
    val titles = listOf<String>("入校报到点", "运动场", "教学楼", "图书馆", "食堂", "快递")
    var placeBasicData = MutableLiveData<PlaceBasicData>()
    val hotWord = MutableLiveData<String>()
    fun retrofitConfig(builder: Retrofit.Builder): Retrofit.Builder {
        builder.baseUrl("http://118.31.20.31:8080/")
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

    fun getPlaceData() {
        ApiGenerator.registerNetSettings(2019212381, { builder -> retrofitConfig(builder) }, { builder -> okHttpConfig(builder) }, true)
        ApiGenerator.getApiService(2019212381,
                ApiService::class.java)
                .getPlaceItemsList()
                .mapOrThrowApiException()
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    placeBasicData.value = it
                }.lifeCycle()
    }
}