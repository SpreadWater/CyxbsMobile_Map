package com.mredrock.cyxbs.discover.map.model.dao

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import okhttp3.ResponseBody
import java.io.File
import java.io.RandomAccessFile

object MapModel {
    fun saveMapImageFile(responseBody: ResponseBody, filePath: String) {
        if (ContextCompat.checkSelfPermission(
                        BaseApp.context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                        BaseApp.context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            com.mredrock.cyxbs.discover.map.utils.Toast.toast("操作失败，请开启储存权限")
            return
        }
        var loadByte: Long = 0
        val file = File(filePath)
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        val buffer = ByteArray(1024 * 1024)
        val randomAccessFile = RandomAccessFile(file, "rwd")
        val loadFileLength: Long = file.length()
        randomAccessFile.seek(loadFileLength)
        while (true) {
            val len: Int = responseBody.byteStream().read(buffer)
            if (len == -1) {
                break
            }
            randomAccessFile.write(buffer, 0, len)
            loadByte += len.toLong()
        }
        randomAccessFile.close()
    }
}