package com.mredrock.cyxbs.discover.map.model


import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException


class DownloadResponseBody(private val responseBody: ResponseBody, private val listener: DownloadListener?) : ResponseBody() {
    private var bufferedSource: BufferedSource? = null
    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = getSource(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    private fun getSource(source: BufferedSource): Source {
        return object : ForwardingSource(source) {
            var downloadBytes = 0L

            @Throws(IOException::class)
            override fun read(buffer: Buffer, byteCount: Long): Long {
                val singleRead = super.read(buffer, byteCount)
                if (-1L != singleRead) {
                    downloadBytes += singleRead
                }
                listener?.onProgress(downloadBytes, responseBody.contentLength(), singleRead == -1L)
                return singleRead
            }
        }
    }
}