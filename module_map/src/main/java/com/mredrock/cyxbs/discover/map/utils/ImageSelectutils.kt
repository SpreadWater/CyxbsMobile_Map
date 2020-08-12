package com.mredrock.cyxbs.discover.map.utils

import android.Manifest
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.discover.map.R
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import org.jetbrains.anko.longToast



/**
 *@date 2020-8-9
 *@author zhangsan
 *@description图片选择
 */
const val REQUEST_CODE_CHOOSE_PHOTO_ALBUM=1
fun AppCompatActivity.selectImageFromAlbum(maxCount: Int,fragment: Fragment) {
    doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA) {
        doAfterGranted {
                Matisse.from(fragment)
                        .choose(MimeType.ofImage(), false)
                        .capture(true)  // 使用相机，和 captureStrategy 一起使用
                        .captureStrategy( CaptureStrategy(true, "RedRock"))
                        .theme(R.style.Matisse_Zhihu)
                        .countable(true)
                        .maxSelectable(maxCount)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.87f)
                        .imageEngine(GlideLoadEngine())
                        .forResult(REQUEST_CODE_CHOOSE_PHOTO_ALBUM)

        }
        doAfterRefused {
            longToast("访问相册失败，原因：未授权")
        }
    }
}
fun AppCompatActivity.selectImageFromAlbum(maxCount: Int,activity: AppCompatActivity) {
    doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA) {
        doAfterGranted {
            Matisse.from(activity)
                    .choose(MimeType.ofImage(), false)
                    .capture(true)  // 使用相机，和 captureStrategy 一起使用
                    .captureStrategy( CaptureStrategy(true, "RedRock"))
                    .theme(R.style.Matisse_Zhihu)
                    .countable(true)
                    .maxSelectable(maxCount)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    .thumbnailScale(0.87f)
                    .imageEngine(GlideLoadEngine())
                    .forResult(REQUEST_CODE_CHOOSE_PHOTO_ALBUM)

        }
        doAfterRefused {
            longToast("访问相册失败，原因：未授权")
        }
    }
}