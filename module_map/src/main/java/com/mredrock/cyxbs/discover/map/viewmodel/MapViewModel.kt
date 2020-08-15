package com.mredrock.cyxbs.discover.map.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.discover.map.BuildConfig
import com.mredrock.cyxbs.discover.map.bean.CollectPlace
import com.mredrock.cyxbs.discover.map.bean.PlaceBasicData
import com.mredrock.cyxbs.discover.map.bean.TabLayoutTitles
import com.mredrock.cyxbs.discover.map.network.ApiService
import com.mredrock.cyxbs.discover.map.utils.Toast
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MapViewModel : BaseViewModel() {
    var typewordPlaceData = MutableLiveData<List<Int>>()
    val tabTitles = MutableLiveData<TabLayoutTitles>()
    val collectPlaces = MutableLiveData<RedrockApiWrapper<CollectPlace>>()
    var placeBasicData = MutableLiveData<PlaceBasicData>()
    val hotWord = MutableLiveData<String>()

    init {
        ApiGenerator.registerNetSettings(2019212381, { builder -> retrofitConfig(builder) }, { builder -> okHttpConfig(builder) }, true)

    }

    fun getTabLayoutTitles() {
        ApiGenerator.getApiService(2019212381, ApiService::class.java)
                .button()
                .mapOrThrowApiException()
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    tabTitles.value = it
                }.lifeCycle()
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

    fun getTypeWordPlaceList(code: String) {
        ApiGenerator.getApiService(2019212381, ApiService::class.java)
                .searchPlaceType(code)
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    typewordPlaceData.value = it
                }.lifeCycle()
    }

    fun getCollectPlace() {
        ApiGenerator.getApiService(2019212381, ApiService::class.java)
                .getCollectPlaceList()
                .setSchedulers()
                .safeSubscribeBy {
                    collectPlaces.value = it
                }.lifeCycle()
    }

    fun getHotWord() {
        ApiGenerator.getApiService(2019212381, ApiService::class.java)
                .getPlaceHot()
                .mapOrThrowApiException()
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    hotWord.value = it
                }.lifeCycle()
    }

    fun getPlaceData() {
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