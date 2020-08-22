package com.mredrock.cyxbs.discover.map.view.activity


import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.DISCOVER_MAP
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.R.layout.map_activity_map
import com.mredrock.cyxbs.discover.map.bean.PlaceBasicData
import com.mredrock.cyxbs.discover.map.bean.PlaceItem
import com.mredrock.cyxbs.discover.map.bean.*
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.database.DataBaseManger
import com.mredrock.cyxbs.discover.map.model.dao.*
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.discover.map.utils.AddIconImage
import com.mredrock.cyxbs.discover.map.utils.NetWorkUtils
import com.mredrock.cyxbs.discover.map.utils.Toast
import com.mredrock.cyxbs.discover.map.view.fragment.PlaceDetailContentFragment
import kotlinx.android.synthetic.main.map_activity_map.*
import kotlinx.android.synthetic.main.map_activity_map.map_iv_image
import java.io.File
import java.lang.Exception
import kotlin.collections.ArrayList

/**
 * @author xgl
 * @date 2020.8
 */

@Route(path = DISCOVER_MAP)
class MapActivity : BaseViewModelActivity<MapViewModel>() {
    companion object {
        const val MAP_SAVE = 1
        const val MSG = 0
        const val MSG_COLLECT = 2
        const val MSG_LOADIMAGE = 3
    }

    private var zoomCenterId: Int = 29
    private var dialogData: ProgressDialog? = null
    private val path = Environment.getExternalStorageDirectory().absolutePath + "/cquptmap/map.jpg"
    private var collectList = ArrayList<PointF>()
    private val placeXList = ArrayList<PlaceItem>()
    private var tabItemList = ArrayList<TabLayoutTitles.TabLayoutItem>()
    override val isFragmentActivity: Boolean
        get() = false

    //搜索返回来的id
    private var placeId: String? = null
    override val viewModelClass: Class<MapViewModel>
        get() = MapViewModel::class.java
    private var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(map_activity_map)
//        //黑夜模式测试
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val userState = ServiceManager.getService(IAccountService::class.java).getVerifyService()
        if (!userState.isLogin()) {
            //这里只是模拟一下登录，如果有并发需求，自己设计
            Thread {
                userState.login(this, "2019212381", "261919")
            }.start()
        }
        //拿到搜索返回的id
        placeId = intent.getStringExtra("placeId")
        initView()
        initData()
    }

    private fun initData() {
        this.doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        {
            doAfterGranted {
                initBasicData()
            }
            doAfterRefused {
                CyxbsToast.makeText(BaseApp.context, R.string.map_toast_open_permission.toString(), android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initBasicData() {

        if (NetWorkUtils.isNetWorkAvailable(this)) {
            getDataFromNetwork()
        } else {
            Toast.toast(R.string.map_toast_open_network)
            getDataFromLocal()
        }
    }

    /*
        从本地拿到数据
     */
    private fun getDataFromLocal() {
        if (File(path).exists()) {
            map_iv_image.setImage(ImageSource.uri(path))
            map_cl_map_background.setBackgroundColor(Color.parseColor(PlaceData.mapData.mapVersion?.let { MapDataDao.getSavedMap(it)?.mapBackgroundColor }))
        } else {
            CyxbsToast.makeText(BaseApp.context, R.string.map_toast_no_map_data.toString(), android.widget.Toast.LENGTH_LONG).show()
        }

        //本地如果有图片就从本地拿取，由于没有网络使用不了，启动锁定模式仅展示地图，取消地图详情
    }

    /*
        从网络拿到地点基础数据
     */
    private fun getDataFromNetwork() {

        //本地有图片直接加载
        if (File(path).exists()) {
            dialogData = ProgressDialog(this)
            if (dialogData != null) {
                dialogData!!.setMessage("加载数据中...")
                dialogData!!.show()
            }
            map_iv_image.setImage(ImageSource.uri(path))
            if (MapDataDao.isMapSaved(MAP_SAVE))
                map_cl_map_background.setBackgroundColor(Color.parseColor(MapDataDao.getSavedMap(MAP_SAVE)?.mapBackgroundColor))
        } else {
            IsLoadImageStatusDao.saveStatus(false)
        }

        // 初始化hot数据
        map_et_search.setText("  大家都在搜:" + SearchData.getSavedHotword())
        viewModel.getHotWord()
        viewModel.hotWord.observe(this, Observer {
            if (it != "" && it != null) {
                SearchData.saveHotword(it)
                map_et_search.setText("  大家都在搜：${it}")
            }
        })

        //初始化 buttoninfo
        viewModel.getTabLayoutTitles()
        viewModel.tabTitles.observe(this, Observer<TabLayoutTitles> {
            ButtonInfoDao.saveButtonInfo(it, true)
            tabItemList = it.buttonInfo as ArrayList<TabLayoutTitles.TabLayoutItem>
            initTabCategory(tabItemList)
        })

        //是否下载图片
        if (!IsLoadImageStatusDao.getStatus()) {
            viewModel.loadMapFile()
            val dialog = com.mredrock.cyxbs.discover.map.view.widget.ProgressDialog(this)
            dialog.show()
            viewModel.mapLoadProgress.observe(this, Observer<Float> {
                dialog.setProgress((it * 100).toInt().toString())
                if ((it * 100).toInt() >= 100 || it == 0f) {
                    if (File(path).exists()) {
                        map_iv_image.setBackgroundColor(Color.parseColor(MapDataDao.getSavedMap(MAP_SAVE)?.mapBackgroundColor))
                        map_iv_image.setImage(ImageSource.uri(path))
                    }
                    IsLoadImageStatusDao.saveStatus(true)
                    dialog.dismiss()
                    dialogData = ProgressDialog(this)
                    if (dialogData != null) {
                        dialogData!!.setMessage("加载数据中...")
                        dialogData!!.show()
                    }
                    sendMsg(MSG_LOADIMAGE)
                }
            })
            dialog.setListener(object : com.mredrock.cyxbs.discover.map.view.widget.ProgressDialog.OnClickListener {
                override fun onCancel() {
                    dialog.setProgress("0")
                    dialog.dismiss()
                    viewModel.disposable?.dispose()
                }
            })
        }

        //初始化placeitem数据
        viewModel.getPlaceData()
        viewModel.placeBasicData.observe(this, Observer<PlaceBasicData> {
            it?.run {

                map_cl_map_background.setBackgroundColor(Color.parseColor(mapBackgroundColor))
                if (!hotWord.equals("") && hotWord != null) {
                    map_et_search.setText("  大家都在搜：$hotWord")
                    SearchData.saveHotword(hotWord!!)
                } else {
                    map_et_search.setText("  大家都在搜:" + SearchData.getSavedHotword())
                }

                //map数据储存
                PlaceData.mapData.apply {
                    mapBackgroundColor = it.mapBackgroundColor
                    mapHeight = it.mapHeight
                    mapUrl = it.mapUrl
                    mapWidth = it.mapWidth
                    openSite = it.openSite
                    mapLoadTime = it.mapLoadTime
                }
                if (!PlaceData.mapData.mapVersion?.let { it1 -> MapDataDao.isMapSaved(it1) }!!) {
                    MapDataDao.saveMap(PlaceData.mapData)
                }

                if (placeList != null) {
                    SearchData.saveItemNum(placeList!!.size)
                }
                zoomCenterId = openSite
                initMap(openSite)
            }
        })

        //初始化收藏数据
        viewModel.getCollectPlace()
        viewModel.collectPlaces.observe(this, Observer<CollectPlace> {
            PlaceData.collectPlace.clear()
            for (placeId in it.placeId!!) {
                PlaceData.collectPlace.add((PlaceData.placeBasicData[placeId - 1]))
            }
            Thread { DataBaseManger.saveAllCollect() }.start()
        })
    }

    /*
      拿到收藏数据
       */

    fun initCollectPlace() {
        collectList.clear()
        Thread {
            DataBaseManger.getAllCollect()
            sendMsg(MSG_COLLECT)
        }.start()
        viewModel.getCollectPlace()
    }

    private fun initView() {

        //锁定操作
        IsLockDao.saveStatus(true)
        if (IsLockDao.getStatus())
            map_iv_lock.setImageResource(R.drawable.map_ic_un_lock)
        map_iv_lock.setOnClickListener {
            if (IsLockDao.getStatus()) {
                map_iv_lock.setImageResource(R.drawable.map_ic_lock)
                Toast.toast(R.string.map_toast_lock)
                IsLockDao.saveStatus(false)
            } else {
                IsLockDao.saveStatus(true)
                map_iv_lock.setImageResource(R.drawable.map_ic_un_lock)
            }
        }

        //代码控制icon
        AddIconImage.setImageViewToButton(R.drawable.map_ic_search_before, map_et_search, 0)

        //初始化点击事件
        map_btn_collect_place.setOnClickListener {
            IsLockDao.saveStatus(true)
            sendMsg(MSG)
            removePins()
            map_iv_image.stopScale()
            initCollectPlace()
        }
        map_et_search.setOnClickListener {
            changeToActivity(SearchActivity())
            IsLockDao.saveStatus(true)
            sendMsg(MSG)
        }
        map_iv_back.setOnClickListener {
            finish()
        }

        //初始化bottom
        bottomSheetBehavior = BottomSheetBehavior.from(map_bottom_sheet_content)
        replaceFragment(PlaceDetailContentFragment(), zoomCenterId)
        map_bottom_sheet_content.visibility = View.VISIBLE
        (bottomSheetBehavior as BottomSheetBehavior<FrameLayout>).state = BottomSheetBehavior.STATE_COLLAPSED
        (bottomSheetBehavior as BottomSheetBehavior<FrameLayout>).addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        bottomSheet.visibility = View.GONE
                    }
                }
            }


            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

    /*
    tab菜单
     */
    private fun initTabCategory(tabItemList: ArrayList<TabLayoutTitles.TabLayoutItem>) {
        map_tl_category.setTitle(tabItemList)
        map_tl_category.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                map_iv_image.stopScale()
                sendMsg(MSG)
                IsLockDao.saveStatus(true)
                if (tab != null) {
                    removePins()
                    for (placeId in tabItemList[tab.position].placeId!!) {
                        val place = PlaceData.placeBasicData[placeId - 1]
                        if (!place.equals(null)) {
                            map_iv_image.setPin(PointF(place.placeCenterX, place.placeCenterY))
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    /*
        定位处理
     */
    private fun placeLocation(placeItem: PlaceItem) {
        removePins()
        if (map_iv_image.isImageLoaded) {
            map_iv_image.setLocation(PointF(placeItem.placeCenterX, placeItem.placeCenterY))
        }
    }

    /*
    初始化map控件
     */
    private fun initMap(id: Int) {
        map_iv_image.setDoubleTapZoomScale(0.5f)
        sendMsg(MSG_LOADIMAGE)
        if (!placeId.equals(null)) {
            Thread { DataBaseManger.getAllPlaces() }.start()
            placeLocation(PlaceData.placeBasicData.get(placeId?.toInt()!! - 1))
        }
        if (!placeId.equals(null)) {
            Thread { DataBaseManger.getAllPlaces() }.start()
            placeLocation(PlaceData.placeBasicData.get(placeId?.toInt()!! - 1))
        }
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val point: PointF? = map_iv_image.viewToSourceCoord(e.x, e.y)
                if (point != null) {
                    removePins()
                    if (IsLockDao.getStatus())
                        judgePlaceX(point)
                }
                LogUtils.d("tagtag", "" + point?.x + " " + point?.y)
                return true
            }
        })
        map_iv_image.setOnTouchListener { view, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }
    }

    fun removePins() {
        map_iv_image.removePins()
    }


    /*
    点击地图判断，先判断x坐标
     */
    private fun judgePlaceX(pointF: PointF) {
        placeXList.clear()
        for (placeX in PlaceData.placeBasicData) {
            if (placeNameX(pointF.x, placeX)) {
                placeXList.add(placeX)
            }
            for (building in placeX.buildingRectList!!) {
                if (placeX(pointF.x, building)) {
                    placeXList.add(placeX)
                }
            }
        }
        judgePlaceY(pointF.y, placeXList)
    }

    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MSG) {
                map_iv_lock.setImageResource(R.drawable.map_ic_un_lock)
            }
            if (msg.what == MSG_COLLECT) {
                for (place in PlaceData.collectPlace) {
                    collectList.add(PointF(place.placeCenterX, place.placeCenterY))
                }
                map_iv_image.addPointF(collectList)
                if (collectList.isEmpty()) {
                    Toast.toast(R.string.map_toast_no_collect_list)
                }
            }
            if (msg.what == MSG_LOADIMAGE) {
                placeLocation(PlaceData.placeBasicData[zoomCenterId - 1])
                if(dialogData!=null)
                    dialogData!!.dismiss()
            }
        }
    }

    fun sendMsg(index: Int) {
        val message = Message()
        message.what = index
        handler.sendMessage(message)
    }

    /*
     点击地图判断，判断y坐标
     */
    private fun judgePlaceY(y: Float, placeList: ArrayList<PlaceItem>) {
        for (placeY in placeList) {
            if (placeNameY(y, placeY)) {
                showPlaceDetail(placeY)
            }
            for (building in placeY.buildingRectList!!) {
                if (placeY(y, building)) {
                    showPlaceDetail(placeY)
                }
            }
        }
    }

    private fun showPlaceDetail(placeItem: PlaceItem) {
        replaceFragment(PlaceDetailContentFragment(), placeItem.placeId)
        map_bottom_sheet_content.visibility = View.VISIBLE
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    /*
        建筑物名字判断
     */
    private fun placeNameX(x: Float, placeItem: PlaceItem): Boolean {
        return x <= placeItem.tagRight && x >= placeItem.tagLeft
    }

    private fun placeNameY(y: Float, placeItem: PlaceItem): Boolean {
        return y <= placeItem.tagBottom && y >= placeItem.tagTop
    }

    /*
        建筑判断
     */
    private fun placeX(x: Float, building: PlaceItem.BuildingRect): Boolean {
        return x <= building.buildingRight && x >= building.buildingLeft
    }

    private fun placeY(y: Float, building: PlaceItem.BuildingRect): Boolean {
        return y <= building.buildingBottom && y >= building.buildingTop
    }


    private fun changeToActivity(activity: Activity) {
        val intent = Intent(BaseApp.context, activity::class.java)
        startActivity(intent)
    }


    private fun replaceFragment(fragment: Fragment, placeId: Int) {
        val bundle = Bundle()
        bundle.putString("placeId", placeId.toString())
        fragment.arguments = bundle
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.map_bottom_sheet_content, fragment)
        transaction.commit()
    }
}
