package com.mredrock.cyxbs.discover.map.view.activity

import android.Manifest
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mredrock.cyxbs.common.config.DIR_PHOTO
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.common.utils.extensions.saveImage
import com.mredrock.cyxbs.common.utils.extensions.setFullScreen
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.discover.map.R
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.map_activity_view_image.*
import org.jetbrains.anko.startActivity

/*
    用于查看图片，保存图片，放大缩小
 */
class ViewImageActivity : AppCompatActivity() {
    companion object {
        private const val IMG_RES_PATHS = "imgResPaths"
        private const val POSITION = "position"

        fun activityStart(context: Context, imgResUrls: Array<String>, position: Int) {
            context.startActivity<ViewImageActivity>(IMG_RES_PATHS to imgResUrls, POSITION to position)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity_view_image)
        setTheme(R.style.Theme_MaterialComponents)
        setFullScreen()
        val url=intent.getIntExtra("url",0)
        map_pv_view_image.setImageResource(url)
        map_pv_view_image.setOnPhotoTapListener { view, x, y ->
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        map_pv_view_image.setOnLongClickListener {
            val drawable = map_pv_view_image.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = (map_pv_view_image.drawable as BitmapDrawable).bitmap
                doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE){
                    doAfterGranted{
                        MaterialAlertDialogBuilder(this@ViewImageActivity)
                                .setTitle("是否保存")
                                .setMessage("这张图片将保存到手机")
                                .setPositiveButton("确定"){
                                    dialog,_->
                                    val name=System.currentTimeMillis().toString() + IMG_RES_PATHS.split('/').lastIndex.toString()
                                    Schedulers.io().scheduleDirect{
                                        this@ViewImageActivity.saveImage(bitmap,name)
                                        MediaScannerConnection.scanFile(this@ViewImageActivity,
                                                arrayOf(Environment.getExternalStorageDirectory().toString() + DIR_PHOTO),
                                                arrayOf("image/jpeg"),
                                                null)
                                    }
                                    runOnUiThread {
                                        toast("图片保存于系统\"$DIR_PHOTO\"文件夹下哦")
                                        dialog.dismiss()
                                        setFullScreen()
                                    }
                                }
                                .setNegativeButton("取消"){
                                    dialog, _ ->
                                    dialog.dismiss()
                                    setFullScreen()
                                }
                                .show()
                    }
                }
            }
            true
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}