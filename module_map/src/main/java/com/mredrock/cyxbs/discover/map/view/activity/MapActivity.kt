package com.mredrock.cyxbs.discover.map.view.activity


import android.Manifest
import android.app.Activity
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
import com.mredrock.cyxbs.discover.map.model.dao.*
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.discover.map.utils.AddIconImage
import com.mredrock.cyxbs.discover.map.utils.Toast
import com.mredrock.cyxbs.discover.map.view.fragment.PlaceDetailContentFragment
import com.mredrock.cyxbs.discover.map.view.widget.ProgressDialog
import kotlinx.android.synthetic.main.map_activity_map.*
import kotlinx.android.synthetic.main.map_activity_map.map_iv_image
import java.io.File
import kotlin.collections.ArrayList

/**
 * @author xgl
 * @date 2020.8
 */

@Route(path = DISCOVER_MAP)
class MapActivity : BaseViewModelActivity<MapViewModel>() {
    companion object {
        const val MAPSAVE = 1
        const val MSG = 0
    }

    private val path = Environment.getExternalStorageDirectory().absolutePath + "/cquptmap/map.jpg"
    private var collectList = ArrayList<PointF>()
    private var zoomCenterId: Int = 29
    private val placeXList = ArrayList<PlaceItem>()
    private var placeItemList = ArrayList<PlaceItem>()
    private var tabItemList = ArrayList<TabLayoutTitles.TabLayoutItem>()
    override val isFragmentActivity: Boolean
        get() = false
    private val map = MapData()
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
        placeId = intent.getStringExtra("placeId")
        initView()
        initBottomSheetBehavior()
        initMapImage()
    }

    private fun initBasicData() {

        if (HistoryPlaceDao.getStatus() && ButtonInfoDao.getStatus()) {
            getDataFromLocal()
        } else {
            getDataFromNetwork()
        }
        getHotWord()
    }

    private fun initMapImage() {
        this.doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        {
            doAfterGranted {
                if (File(path).exists()) {
                    map_iv_image.setImage(ImageSource.uri(path))
                    if (MapDataDao.isMapSaved(MAPSAVE))
                        map_cl_map_background.setBackgroundColor(Color.parseColor(MapDataDao.getSavedMap(1)?.mapBackgroundColor))
                } else {
                    Toast.toast("由于网络不稳定，如进入出现白屏，退出重新登录直到全部数据出现")
                    HistoryPlaceDao.saveStatus(false)
                    ButtonInfoDao.saveStatus(false)
                }
                initBasicData()
            }
            doAfterRefused {
                CyxbsToast.makeText(BaseApp.context, "操作失败，请开启储存权限", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun initLoadMapProgress() {
        val dialog = ProgressDialog(this)
        dialog.show()
        viewModel.mapLoadProgress.observe(this, Observer<Float> {
            dialog.setProgress((it * 100).toInt().toString())
            if ((it * 100).toInt() >= 100 || it == 0f) {
                val path = Environment.getExternalStorageDirectory().absolutePath + "/cquptmap/map.jpg"
                if (File(path).exists()) {
                    map_iv_image.setImage(ImageSource.uri(path))
                }
                IsLoadImageStatusDao.saveStatus(true)
                dialog.dismiss()
            }
        })
        dialog.setListener(object : ProgressDialog.OnClickListener {
            override fun onCancel() {
                dialog.setProgress("0")
                dialog.dismiss()
                viewModel.disposable?.dispose()
            }
        })
    }

    /*
    拿到收藏数据
     */

    fun getCollectPlaceData() {
        collectList.clear()
        map_iv_image.clearPointList()
        map_iv_image.stopScale()
        viewModel.getCollectPlace()
        viewModel.collectPlaces.observe(this, Observer<CollectPlace> {
            for (placeId in it.placeId!!) {
                if (CollectStatusDao.getCollectStatus(placeId)) {
                    val place = HistoryPlaceDao.getSavedPlace(placeId)
                    if (place != null) {
                        collectList.add(PointF(place.placeCenterX, place.placeCenterY))
                    }
                }
            }
        })
        if (collectList.isEmpty()) {
            Toast.toast("啊哦，你目前没有收藏哦")
        } else {
            map_iv_image.clearPointList()
            map_iv_image.stopScale()
            map_iv_image.addPointF(collectList)
        }
    }

    /*
    得到hotword
     */
    fun getHotWord() {
        map_et_search.setText("  大家都在搜:" + SearchData.getSavedHotword())
        viewModel.getHotWord()
        viewModel.hotWord.observe(this, Observer {
            if (it != "" && it != null) {
                SearchData.saveHotword(it)
                map_et_search.setText("  大家都在搜：${it}")
            }
        })
    }

    /*
        从本地拿到数据
     */
    private fun getDataFromLocal() {
        val path = Environment.getExternalStorageDirectory().absolutePath + "/cquptmap/map.jpg"
        if (File(path).exists()) {
            map_iv_image.setImage(ImageSource.uri(path))
            map_cl_map_background.setBackgroundColor(Color.parseColor(map.mapVersion?.let { MapDataDao.getSavedMap(it)?.mapBackgroundColor }))
        } else {
            CyxbsToast.makeText(BaseApp.context, "本地没有地图数据", android.widget.Toast.LENGTH_LONG).show()
        }
        var num = 1
        while (num <= HistoryPlaceDao.getAllSavePlace()) {
            HistoryPlaceDao.getSavedPlace(num)?.let { placeItemList.add(it) }
            num++
        }
        tabItemList = ButtonInfoDao.getSavedButtonInfo()?.buttonInfo as ArrayList<TabLayoutTitles.TabLayoutItem>
        initTabCategory(tabItemList)
        initMapData(placeItemList)
    }

    /*
        从网络拿到地点基础数据
     */
    private fun getDataFromNetwork() {
        viewModel.getTabLayoutTitles()
        viewModel.tabTitles.observe(this, Observer<TabLayoutTitles> {
            ButtonInfoDao.saveButtonInfo(it, true)
            tabItemList = it.buttonInfo as ArrayList<TabLayoutTitles.TabLayoutItem>
            initTabCategory(tabItemList)
        })
        if (!IsLoadImageStatusDao.getStatus()) {
            initLoadMapProgress()
        }
        viewModel.getPlaceData()
        viewModel.placeBasicData.observe(this, Observer<PlaceBasicData> {
            it?.run {
                if (!File(path).exists()) {
                    mapUrl?.let { it1 -> viewModel.loadMapFile(it1) }
                }
                map_cl_map_background.setBackgroundColor(Color.parseColor(mapBackgroundColor))
                if (!hotWord.equals("") && hotWord != null) {
                    map_et_search.setText("  大家都在搜：$hotWord")
                    SearchData.saveHotword(hotWord!!)
                } else {
                    map_et_search.setText("  大家都在搜:" + SearchData.getSavedHotword())
                }
                //map数据储存
                map.apply {
                    mapBackgroundColor = it.mapBackgroundColor
                    mapHeight = it.mapHeight
                    mapUrl = it.mapUrl
                    mapWidth = it.mapWidth
                    openSite = it.openSite
                    mapLoadTime = it.mapLoadTime
                }
                if (!map.mapVersion?.let { it1 -> MapDataDao.isMapSaved(it1) }!!) {
                    MapDataDao.saveMap(map)
                }
                //保存到本地数据库
                if (placeList != null) {
                    SearchData.saveItemNum(placeList!!.size)
                    placeItemList = placeList as ArrayList<PlaceItem>
                    for (place in it.placeList!!) {
                        if (!HistoryPlaceDao.isPlaceSaved(place.placeId)) {
                            HistoryPlaceDao.savePlace(place)
                        }
                    }
                    initMapData(it.placeList as ArrayList<PlaceItem>)
                    HistoryPlaceDao.saveStatus(true)
                }
                zoomCenterId = openSite
            }
        })
    }

    private fun initView() {
        map_iv_lock.setOnClickListener {
            if (IsLockDao.getStatus()) {
                map_iv_lock.setImageResource(R.drawable.map_ic_lock)
                Toast.toast("取消锁定后对地图进行操作")
                IsLockDao.saveStatus(false)
            } else {
                IsLockDao.saveStatus(true)
                map_iv_lock.setImageResource(R.drawable.map_ic_un_lock)
            }
        }
        AddIconImage.setImageViewToButton(R.drawable.map_ic_search_before, map_et_search, 0)
        map_btn_collect_place.setOnClickListener {
            IsLockDao.saveStatus(true)
            sendMsg(MSG)
            getCollectPlaceData()
        }
        map_et_search.setOnClickListener {
            changeToActivity(SearchActivity())
            IsLockDao.saveStatus(true)
            sendMsg(MSG)
            finish()
        }

    }

    /*
    bottom初始化
     */
    private fun initBottomSheetBehavior() {
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
                IsLockDao.saveStatus(true)
                sendMsg(MSG)
                if (tab != null) {
                    tabItemList[tab.position].code?.let { viewModel.getTypeWordPlaceList(it) }
                }
                viewModel.typewordPlaceData.observe(this@MapActivity, Observer {
                    map_iv_image.clearPointList()
                    map_iv_image.stopScale()
                    if (it != null) {
                        for (placeId in it) {
                            val place = HistoryPlaceDao.getSavedPlace(placeId)
                            if (place != null) {
                                map_iv_image.setPin(PointF(place.placeCenterX, place.placeCenterY))
                            }
                        }
                    }
                })
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
    private fun PlaceLocation(placeItem: PlaceItem) {
        map_iv_image.clearPointList()
        map_iv_image.setLocation(0.5f, PointF(placeItem.placeCenterX, placeItem.placeCenterY))

    }

    /*
    初始化map控件
     */
    private fun initMapData(placeList: ArrayList<PlaceItem>) {
        map_iv_image.setDoubleTapZoomScale(0.5f)
        PlaceLocation(placeList[zoomCenterId - 1])
        if (placeId != null) {
            HistoryPlaceDao.getSavedPlace(placeId!!.toInt())?.let { PlaceLocation(it) }
            HistoryPlaceDao.getSavedPlace(placeId!!.toInt())?.let { showPlaceDetail(it) }
        }
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (map_iv_image.isReady) {
                    map_iv_image.clearPointList()
                    val point: PointF? = map_iv_image.viewToSourceCoord(e.x, e.y)
                    if (point != null) {
                        if (IsLockDao.getStatus())
                            judgePlaceX(point)
                    }
                    LogUtils.d("tagtag", "" + point?.x + " " + point?.y)
                }
                return true
            }
        })
        map_iv_image.setOnTouchListener { view, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }
    }


    /*
    点击地图判断，先判断x坐标
     */
    private fun judgePlaceX(pointF: PointF) {
        placeXList.clear()
        for (placeX in placeItemList) {
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
