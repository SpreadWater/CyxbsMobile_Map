package com.mredrock.cyxbs.discover.map.model


import androidx.annotation.NonNull
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class DownloadInterceptor(listener: DownloadListener) : Interceptor {
    private val listener: DownloadListener = listener

    @Throws(IOException::class)
    override fun intercept(@NonNull chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())
        return originalResponse.newBuilder()
                .body(originalResponse.body?.let { DownloadResponseBody(it, listener) })
                .build()
    }

}