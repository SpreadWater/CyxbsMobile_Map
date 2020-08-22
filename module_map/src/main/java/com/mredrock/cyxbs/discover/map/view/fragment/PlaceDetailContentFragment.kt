package com.mredrock.cyxbs.discover.map.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.database.DataBaseManger
import com.mredrock.cyxbs.discover.map.model.dao.CollectStatusDao
import com.mredrock.cyxbs.discover.map.utils.ImageSelectutils
import com.mredrock.cyxbs.discover.map.view.activity.ImageAllActivity
import com.mredrock.cyxbs.discover.map.view.adapter.PlaceDetailImageAdapter
import com.mredrock.cyxbs.discover.map.view.adapter.PlaceLabelAdapter
import com.mredrock.cyxbs.discover.map.view.widget.CollectDialog
import com.mredrock.cyxbs.discover.map.view.widget.ShareDialog
import com.mredrock.cyxbs.discover.map.viewmodel.ImageLoaderViewModel
import com.mredrock.cyxbs.discover.map.viewmodel.PlaceDetailViewModel
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.map_fragment_place_content.*
import java.io.File

/**
 * @author xgl
 * @date 2020.8
 */
class PlaceDetailContentFragment : BaseViewModelFragment<PlaceDetailViewModel>() {
    var placeId: Int? = 0
    private val ATTRIBUTE = 0
    private val LABEL = 1
    var mplaceattribute = ""
    val imageurls = ArrayList<String>()
    override val viewModelClass: Class<PlaceDetailViewModel>
        get() = PlaceDetailViewModel::class.java

    val LoadviewModel = ImageLoaderViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.map_fragment_place_content, container, false)
        val bundle = this.arguments //得到从Activity传来的数据
        if (bundle != null) {
            placeId = bundle.getString("placeId").toInt()
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        placeId?.let { getPlaceDetail(it) }
        initOnClick()

    }

    fun getPlaceDetail(placeId: Int) {
        viewModel.getPlaceDetail(placeId)
        viewModel.placeItemDetail.observe(viewLifecycleOwner, Observer {
            it?.run {
                if (placeAttribute != null && !placeAttribute!!.contains("")) {
                    initLabelRV(placeAttribute as ArrayList<String>, ATTRIBUTE)
                    mplaceattribute = placeAttribute!!.get(0)
                }
                map_tv_place_name.text = it.placeName
                sendMsg(1)
                imageurls.addAll(images!!)
                initImagesRv(images as ArrayList<String>)
            }
        })
    }

    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                placeId?.let { isCollect(it) }
            }
        }
    }

    fun sendMsg(index: Int) {
        val message = Message()
        message.what = index
        handler.sendMessage(message)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === ImageSelectutils.REQUEST_CODE_CHOOSE_PHOTO_ALBUM && resultCode === Activity.RESULT_OK) {
            //图片路径 同样视频地址也是这个 根据requestCode
            val pathList: List<Uri> = Matisse.obtainResult(data)
            for (_Uri in pathList) {
                val file = File(_Uri.toString())
                LoadviewModel.loadImage(file, placeId!!)
                System.out.println(_Uri.path)
            }
        }
    }

    /*
     判断是否被收藏
     */
    private fun isCollect(placeId: Int) {
        if (CollectStatusDao.getCollectStatus(placeId))
            map_iv_place_collect.setImageResource(R.drawable.map_ic_collect_red)
        else
            map_iv_place_collect.setImageResource(R.drawable.map_ic_collect)
    }

    private fun initOnClick() {
        map_tv_search_more_place_detail.setOnClickListener {
            placeId?.let { it1 -> changeToActivity(ImageAllActivity(), it1, imageurls) }
        }
        map_iv_place_collect.setOnClickListener {
            if (!placeId?.let { it1 -> CollectStatusDao.getCollectStatus(it1) }!!) {
                Thread { DataBaseManger.insertCollectByPlaceId(PlaceData.placeBasicData[placeId!! - 1]) }.start()
                viewModel.addCollectPlace(placeId!!)
                CollectStatusDao.saveCollectStatus(placeId!!, true)
                map_iv_place_collect.setImageResource(R.drawable.map_ic_collect_red)

            } else {
                this.activity?.let { it1 -> showCollectDialog(it1) }
            }
        }
        map_tv_share_image.setOnClickListener {
            this.activity?.let { it1 -> showShareDialog(it1) }
        }

    }


    private fun initLabelRV(placeLabelList: ArrayList<String>, type: Int) {
        when (type) {
            ATTRIBUTE -> {
                val placeAttributeAdapter = PlaceLabelAdapter(placeLabelList)
                map_rrv_place_label.adapter = placeAttributeAdapter
            }
            LABEL -> {
                val placeLabelAdapter = PlaceLabelAdapter(placeLabelList)
                map_rrv_place_detail_label.adapter = placeLabelAdapter
            }
        }
    }

    private fun initImagesRv(imageUrlList: ArrayList<String>) {
        if (imageUrlList.isNotEmpty()) {
            val placeDetailImageAdapter = PlaceDetailImageAdapter(imageUrlList)
            map_rv_place_detail_image_list.apply {
                layoutManager = LinearLayoutManager(BaseApp.context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(DividerItemDecoration(BaseApp.context, DividerItemDecoration.VERTICAL))
                adapter = placeDetailImageAdapter
            }
        }
    }

    private fun changeToActivity(activity: Activity, placeId: Int, imageUrls: ArrayList<String>) {
        val intent = Intent(BaseApp.context, activity::class.java)
        intent.putExtra("sharePlaceid", placeId)
        intent.putExtra("imageurl", imageUrls)
        this.startActivity(intent)
    }

    private fun showShareDialog(activity: Activity) {

        val dialog = ShareDialog(activity)
        dialog.setListener(object : ShareDialog.OnClickListener {
            override fun onCancel() {
                dialog.dismiss()
            }

            override fun onConfirm() {
                dialog.dismiss()
                openAlbum()
            }
        })
        dialog.show()
    }

    private fun showCollectDialog(activity: Activity) {

        val dialog = CollectDialog(activity)
        dialog.setListener(object : CollectDialog.OnClickListener {
            override fun onCancel() {
                dialog.dismiss()
            }

            override fun onConfirm() {
                Thread { placeId?.let { DataBaseManger.deleteCollectByPlaceId(it) } }.start()
                viewModel.deleteCollectPlace(placeId!!)
                map_iv_place_collect.setImageResource(R.drawable.map_ic_collect)
                CollectStatusDao.saveCollectStatus(placeId!!, false)
                dialog.dismiss()
            }
        })
        dialog.show()
    }

    private fun openAlbum() {
        ImageSelectutils.selectImageFromAlbum(activity as AppCompatActivity, 9, this)
    }
}
