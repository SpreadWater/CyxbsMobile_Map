package com.mredrock.cyxbs.discover.map.viewmodel

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.discover.map.BuildConfig
import com.mredrock.cyxbs.discover.map.bean.CollectPlace
import com.mredrock.cyxbs.discover.map.bean.PlaceBasicData
import com.mredrock.cyxbs.discover.map.bean.PlaceItem
import com.mredrock.cyxbs.discover.map.bean.TabLayoutTitles
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.database.DataBaseManger
import com.mredrock.cyxbs.discover.map.database.PlaceDatabase
import com.mredrock.cyxbs.discover.map.model.DownloadInterceptor
import com.mredrock.cyxbs.discover.map.model.DownloadListener
import com.mredrock.cyxbs.discover.map.model.dao.MapDataDao
import com.mredrock.cyxbs.discover.map.model.dao.MapModel
import com.mredrock.cyxbs.discover.map.network.ApiService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MapViewModel : BaseViewModel() {
    val mapLoadProgress = MutableLiveData<Float>()
    var disposable: Disposable? = null
    val tabTitles = MutableLiveData<TabLayoutTitles>()
    var placeBasicData = MutableLiveData<PlaceBasicData>()
    val hotWord = MutableLiveData<String>()

    companion object {
        const val TAG = "MapViewModel"
    }

    fun retrofitConfig(builder: Retrofit.Builder): Retrofit.Builder {
        builder.baseUrl(PlaceData.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        return builder
    }

    fun OkHttpDownloadConfig(builder: okhttp3.OkHttpClient.Builder): okhttp3.OkHttpClient.Builder {
        builder.run {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
            addInterceptor(DownloadInterceptor(object : DownloadListener {
                override fun onProgress(currentByte: Long, totalByte: Long, done: Boolean) {
                    mapLoadProgress.postValue((currentByte.toDouble() / totalByte).toFloat())
                }
            }))
        }
        return builder
    }

    init {
        ApiGenerator.registerNetSettings(2019212381, { builder -> retrofitConfig(builder) }, null, true)
    }

    fun getTabLayoutTitles() {
        ApiGenerator.getApiService(2019212381, ApiService::class.java)
                .button()
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    tabTitles.value = it
                }
    }

    fun getCollectPlace() {
        ApiGenerator.getApiService(2019212381, ApiService::class.java)
                .getCollectPlaceList()
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    PlaceData.collectPlace.clear()
                    it.placeId?.forEach { id ->
                        PlaceData.collectPlace.add(PlaceData.placeBasicData[id - 1])
                    }
                    Thread { DataBaseManger.saveAllCollect() }.start()
                }
    }

    fun getHotWord() {
        ApiGenerator.getApiService(2019212381, ApiService::class.java)
                .getPlaceHot()
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    hotWord.value = it
                }
    }

    fun getPlaceData() {
        ApiGenerator.getApiService(2019212381,
                ApiService::class.java)
                .getPlaceItemsList()
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    Thread {
                        PlaceData.placeBasicData.clear()
                        PlaceData.placeBasicData = it.placeList as ArrayList<PlaceItem>
                        DataBaseManger.saveAllPlaces()
                    }.start()
                    placeBasicData.value = it
                }
    }

    fun loadMapFile() {
        ApiGenerator.registerNetSettings(2019212381, { builder -> retrofitConfig(builder) }, { builder -> OkHttpDownloadConfig(builder) }, true)
        MapDataDao.getSavedMap(1)?.mapUrl?.let {
            ApiGenerator.getApiService(2019212381, ApiService::class.java)
                    .download(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnNext {
                        val path = Environment.getExternalStorageDirectory().absolutePath + "/cquptmap/map.jpg"
                        MapModel.saveMapImageFile(it, path)
                    }
                    .doOnError { it ->
                        LogUtils.d(TAG, "load image failed")
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ResponseBody> {
                        override fun onSubscribe(d: Disposable) {
                            disposable = d
                        }

                        override fun onNext(responseBody: ResponseBody) {

                        }

                        override fun onError(e: Throwable) {
                            LogUtils.d(TAG, "error message" + e.message)
                        }

                        override fun onComplete() {

                        }

                    })
        }
    }
}