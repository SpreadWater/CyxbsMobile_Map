package com.mredrock.cyxbs.discover.map.model


interface DownloadListener {

    fun onProgress(currentByte: Long, totalByte: Long, done: Boolean)

}