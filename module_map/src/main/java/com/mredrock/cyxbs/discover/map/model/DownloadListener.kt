package com.mredrock.cyxbs.discover.map.model

import anetwork.channel.download.DownloadManager
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import java.io.File


interface DownloadListener {

    fun onProgress(currentByte: Long, totalByte: Long, done: Boolean)

}