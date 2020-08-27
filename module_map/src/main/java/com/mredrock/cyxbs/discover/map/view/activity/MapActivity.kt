package com.mredrock.cyxbs.discover.map.view.activity


import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.DISCOVER_MAP
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.R.layout.map_activity_map
import com.mredrock.cyxbs.discover.map.bean.PlaceBasicData
import com.mredrock.cyxbs.discover.map.bean.*
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.database.DataBaseManger
import com.mredrock.cyxbs.discover.map.event.PlaceIdEvent
import com.mredrock.cyxbs.discover.map.model.dao.*
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.discover.map.utils.AddIconImage
import com.mredrock.cyxbs.discover.map.utils.NetWorkUtils
import com.mredrock.cyxbs.discover.map.utils.Toast
import com.mredrock.cyxbs.discover.map.view.fragment.PlaceDetailContentFragment
import com.mredrock.cyxbs.discover.map.view.widget.MyMapLayout
import kotlinx.android.synthetic.main.map_activity_map.*
import kotlinx.android.synthetic.main.map_activity_map.map_iv_image
import org.greenrobot.eventbus.EventBus
import java.io.File
import kotlin.collections.ArrayList

/**
 *
 */

@Route(path = DISCOVER_MAP)
class MapActivity : BaseViewModelActivity<MapViewModel>() {
    companion object {
        const val MAP_SAVE = 1
        const val MSG = 0
        const val MSG_COLLECT = 2
    }

    private val iconList = mutableListOf<IconBean>()
    private var dialogData: ProgressDialog? = null
    private var zoomCenterId: Int = 29
    private val path = Environment.getExternalStorageDirectory().absolutePath + "/cquptmap/map.jpg"
    private var collectList = ArrayList<Int>()
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
            Thread {
                userState.login(this, "2019212381", "261919")
            }.start()
        }
        dialogData = ProgressDialog(this)
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
            map_iv_image.setUrl("loadFromLocal")
//            Toast.toast("由于没有网络，仅展示地图")
            map_cl_map_background.setBackgroundColor(Color.parseColor(PlaceData.mapData.mapVersion?.let { MapDataDao.getSavedMap(it)?.mapBackgroundColor }))
        } else {
            Toast.toast(R.string.map_toast_no_map_data)
        }

        //本地如果有图片就从本地拿取，由于没有网络使用不了，启动锁定模式仅展示地图，取消地图详情
    }

    /*
        从网络拿到地点基础数据
     */
    private fun getDataFromNetwork() {

        //本地有图片直接加载
        if (File(path).exists()) {
            dialogData?.setMessage("加载数据中...")
            dialogData?.show()
            if (MapDataDao.isMapSaved(MAP_SAVE))
                map_cl_map_background.setBackgroundColor(Color.parseColor(MapDataDao.getSavedMap(MAP_SAVE)?.mapBackgroundColor))
        } else {
            IsLoadImageStatusDao.saveStatus(false)
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
            }
            if (IsLoadImageStatusDao.getStatus())
                sendMsg(MAP_SAVE)
        })

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

        val dialog = com.mredrock.cyxbs.discover.map.view.widget.ProgressDialog(this)
        //是否下载图片
        if (!IsLoadImageStatusDao.getStatus()) {
            viewModel.loadMapFile()
            dialog.show()
        }

        //监听进度条
        viewModel.mapLoadProgress.observe(this, Observer<Float> {
            dialog.setProgress((it * 100).toInt().toString())
            if ((it * 100).toInt() >= 100 || it == 0f) {
                if (File(path).exists())
                    map_iv_image.setBackgroundColor(Color.parseColor(MapDataDao.getSavedMap(MAP_SAVE)?.mapBackgroundColor))
                IsLoadImageStatusDao.saveStatus(true)
                sendMsg(MAP_SAVE)
                dialog.dismiss()
            }
        })

        dialog.setListener(object : com.mredrock.cyxbs.discover.map.view.widget.ProgressDialog.OnClickListener {
            override fun onCancel() {
                dialog.setProgress("0")
                dialog.dismiss()
                viewModel.disposable?.dispose()
            }
        })

        //初始化收藏数据
        viewModel.getCollectPlace()
    }

    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MSG) {
                map_iv_lock.setImageResource(R.drawable.map_ic_un_lock)
            }
            if (msg.what == MSG_COLLECT) {
                PlaceData.collectPlace.forEach { place ->
                    collectList.add(place.placeId)
                }
                if (collectList.isEmpty())
                    Toast.toast(R.string.map_toast_no_collect_list)
                else
                    map_iv_image.showSomeIcons(collectList)
            }
            if (msg.what == MAP_SAVE)
                initMap()
        }
    }

    fun sendMsg(index: Int) {
        val message = Message()
        message.what = index
        handler.sendEmptyMessageDelayed(index, 300)
    }

    /*
      拿到收藏数据
       */
    fun initCollectPlace() {
        closeIcons()
        collectList.clear()
        Thread {
            DataBaseManger.getAllCollect()
            sendMsg(MSG_COLLECT)
        }.start()

        viewModel.getCollectPlace()
    }

    private fun initView() {

        if (IsLockDao.getStatus())
            map_iv_lock.setImageResource(R.drawable.map_ic_un_lock)
        map_iv_lock.setOnClickListener {
            if (IsLockDao.getStatus()) {
                map_iv_lock.setImageResource(R.drawable.map_ic_lock)
                map_iv_image.setIsLock(true)
                IsLockDao.saveStatus(false)
            } else {
                map_iv_image.setIsLock(false)
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
            map_iv_image.setIsLock(false)
            initCollectPlace()
        }
        map_et_search.setOnClickListener {
            changeToActivity(SearchActivity())
            sendMsg(MSG)
            map_iv_image.setIsLock(false)
            IsLockDao.saveStatus(true)
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
                IsLockDao.saveStatus(true)
                if (tab != null) {
                    sendMsg(MSG)
                    map_iv_image.setIsLock(false)
                    closeIcons()
                    map_iv_image.showSomeIcons(tabItemList[tab.position].placeId as ArrayList<Int>)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    /*
        初始化地图数据
     */
    private fun initMap() {
        dialogData?.dismiss()
        iconList.clear()
        PlaceData.placeBasicData.forEach { bean ->
            val buildingList = bean.buildingRectList
            buildingList?.forEach { building ->
                iconList.add(IconBean(bean.placeId,
                        bean.placeCenterX,
                        bean.placeCenterY,
                        bean.tagLeft,
                        bean.tagRight,
                        bean.tagTop,
                        bean.tagBottom,
                        building.buildingLeft,
                        building.buildingRight,
                        building.buildingTop,
                        building.buildingBottom
                ))
            }
        }
        if (File(path).exists())
            map_iv_image.setUrl("loadFromLocal")
        map_iv_image.addSomeIcons(iconList)
        map_iv_image.setOpenSiteId(zoomCenterId.toString())
        map_iv_image.showIconWithoutAnim(zoomCenterId.toString())
        map_iv_image.setMyOnIconClickListener(object : MyMapLayout.OnIconClickListener {
            override fun onIconClick(v: View) {
                val iconBean = v.tag as IconBean
                showPlaceDetail(iconBean.placeId)
                map_iv_image.closeIcon(v as ImageView)
            }
        })

        map_iv_image.setMyOnPlaceClickListener(object : MyMapLayout.OnPlaceClickListener {

            override fun onPlaceClick(v: View) {
                val iconBean = v.tag as IconBean
                showPlaceDetail(iconBean.placeId)
                map_iv_image.showIcon(iconBean.placeId.toString())
            }
        })
    }

    fun closeIcons() {
        map_iv_image.closeAllIcon()
    }

    private fun showPlaceDetail(placeId: Int) {
        replaceFragment(PlaceDetailContentFragment(), placeId)
        map_bottom_sheet_content.visibility = View.VISIBLE
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun changeToActivity(activity: Activity) {
        val intent = Intent(BaseApp.context, activity::class.java)
        startActivity(intent)
    }

    private fun replaceFragment(fragment: Fragment, placeId: Int) {
        EventBus.getDefault().postSticky(PlaceIdEvent(placeId))
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.map_bottom_sheet_content, fragment)
        transaction.commit()
    }
}
