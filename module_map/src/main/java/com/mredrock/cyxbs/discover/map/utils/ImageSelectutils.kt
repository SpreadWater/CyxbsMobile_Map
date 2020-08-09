package com.mredrock.cyxbs.discover.map.utils

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.discover.map.R
import org.jetbrains.anko.longToast
import top.limuyang2.photolibrary.activity.LPhotoPickerActivity
import top.limuyang2.photolibrary.engine.LGlideEngine
import top.limuyang2.photolibrary.util.LPPImageType


/**
 *@date 2020-8-9
 *@author zhangsan
 *@description图片选择
 */
const val CHOOSE_PHOTO_REQUEST = 0x1024

fun AppCompatActivity.selectImageFromAlbum(maxCount: Int, selected: ArrayList<String>?) {
    doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
        doAfterGranted {
            val intent = LPhotoPickerActivity.IntentBuilder(this@selectImageFromAlbum)
                    .maxChooseCount(maxCount)
                    .columnsNumber(4)
                    .imageType(LPPImageType.ofAll())
                    .imageEngine(LGlideEngine())
                    .selectedPhotos(selected)
                    .theme(R.style.map_LPhotoTheme)
                    .build()
            startActivityForResult(intent, CHOOSE_PHOTO_REQUEST)
        }

        doAfterRefused {
            longToast("访问相册失败，原因：未授权")
        }
    }
}