package com.mredrock.cyxbs.discover.map.view.activity


import android.app.Activity
import android.content.Intent
import android.graphics.PointF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.davemorrissey.labs.subscaleview.ImageSource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DISCOVER_MAP
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.R.layout.map_activity_map
import com.mredrock.cyxbs.discover.map.bean.PlaceBasicData
import com.mredrock.cyxbs.discover.map.bean.PlaceItem
import com.mredrock.cyxbs.discover.map.model.dao.HistoryPlaceDao
import com.mredrock.cyxbs.discover.map.bean.*
import com.mredrock.cyxbs.discover.map.model.dao.SearchData
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.discover.map.utils.AddIconImage
import com.mredrock.cyxbs.discover.map.utils.Toast
import com.mredrock.cyxbs.discover.map.view.fragment.PlaceDetailContentFragment
import kotlinx.android.synthetic.main.map_activity_map.*
import kotlinx.android.synthetic.main.map_activity_map.map_iv_image
import kotlin.collections.ArrayList


@Route(path = DISCOVER_MAP)
class MapActivity : BaseViewModelActivity<MapViewModel>() {
    private val collectPointFList = ArrayList<PointF>()
    private val placeXList = ArrayList<PlaceItem>()
    private var placeItemList = ArrayList<PlaceItem>()
    private var tabItemList = ArrayList<TabLayoutTitles.TabLayoutItem>()
    override val isFragmentActivity: Boolean
        get() = false
    override val viewModelClass: Class<MapViewModel>
        get() = MapViewModel::class.java
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(map_activity_map)
        val userState = ServiceManager.getService(IAccountService::class.java).getVerifyService()
        if (!userState.isLogin()) {
            //这里只是模拟一下登录，如果有并发需求，自己设计
            Thread {
                userState.login(this, "2019212381", "261919")
            }.start()
        }
        map_iv_image.setImage(ImageSource.resource(R.drawable.map_ic_background))
        initAddViewToIcon()
        initTabCategory()
        getHotWord()
        getPlaceItemDataFromLocal()
        initBottomSheetBehavior()
        initIconClick()
    }

    /*
        拿到收藏数据
     */

    fun getCollectPlaceData() {
        viewModel.getCollectPlace()
        viewModel.collectPlaces.observe(this, Observer<CollectPlace> {
            for (collectPlace in it.placeId!!) {
                val place = collectPlace?.let { it1 -> HistoryPlaceDao.getSavedPlace(it1) }
                if (place != null) {
                    collectPointFList.add(PointF(place.placeCenterX, place.placeCenterY))
                }
            }
            if (!collectPointFList.isEmpty()) {
                map_iv_image.clearPointList()
                map_iv_image.stopScale()
                map_iv_image.addPointF(collectPointFList)
            } else {
                Toast.toast("啊哦，你还未收场地点")
            }
        })
    }

    /*
    得到hotword
     */
    fun getHotWord() {
        viewModel.getHotWord()
        viewModel.hotWord.observe(this, Observer {

            map_et_search.setText("  大家都在搜：${it}")
        })
    }

    /*
        从本地拿到数据
     */
    private fun getPlaceItemDataFromLocal() {
        var num = 1
        if (HistoryPlaceDao.getAllSavePlace() != 0) {
            while (num <= HistoryPlaceDao.getAllSavePlace()) {
                HistoryPlaceDao.getSavedPlace(num)?.let { placeItemList.add(it) }
                LogUtils.d("placelist", placeItemList[num - 1].placeName.toString())
                num++
            }
            initMapView(placeItemList)
        } else
            getPlaceItemDataFromNetwork()
    }

    /*
        从网络拿到地点基础数据
     */
    private fun getPlaceItemDataFromNetwork() {
        viewModel.getPlaceData()
        viewModel.placeBasicData.observe(this, Observer<PlaceBasicData> {
            it?.run {
                if (!hotWord.equals("")) {
                    if (hotWord != null) {
                        SearchData.saveHotword(hotWord!!)
                    }
                    map_et_search.setText("  大家都在搜：$hotWord")
                } else {
                    map_et_search.setText("  大家都在搜：红岩网校")
                }
                if (placeList != null) {
                    SearchData.saveItemNum(placeList!!.size)
                    placeItemList = placeList as ArrayList<PlaceItem>
                    for (place in it.placeList!!) {
                        //保存到本地数据库
                        //判断是否已经保持
                        if (!HistoryPlaceDao.isPlaceSaved(place.placeId)) {
                            HistoryPlaceDao.savePlace(place)
                        }
                    }
                    initMapView(placeItemList)
                }
            }
        })
    }

    private fun initAddViewToIcon() {
        AddIconImage.setImageViewToButton(R.drawable.map_ic_search_before, map_et_search, 0)

    }

    /*
    bottom初始化
     */
    private fun initBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(map_bottom_sheet_content)
        replaceFragment(PlaceDetailContentFragment(), 29,"腾飞门（新校门）")
        map_bottom_sheet_content.visibility = View.VISIBLE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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
        一些简单控件的点击事件
         */
    fun initIconClick() {
        map_btn_collect_place.setOnClickListener {
            getCollectPlaceData()
        }
        map_et_search.setOnClickListener {
            changeToActivity(SearchActivity())
        }
    }

    /*
    tab菜单
     */
    private fun initTabCategory() {
        viewModel.getTabLayoutTitles()
        viewModel.tabTitles.observe(this, Observer<TabLayoutTitles> {
            tabItemList = it.buttonInfo as ArrayList<TabLayoutTitles.TabLayoutItem>
            map_tl_category.setTitle(tabItemList)
        })
        map_tl_category.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    tabItemList.get(tab.position).code?.let { viewModel.getTypeWordPlaceList(it) }
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
    初始化map控件
     */
    private fun initMapView(placeList: ArrayList<PlaceItem>) {
        if (placeList.isEmpty())
            getPlaceItemDataFromLocal()
        map_iv_image.clearPointList()
        map_iv_image.setLocation(0.5f, PointF(placeList[28].placeCenterX, placeList[28].placeCenterY))
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (map_iv_image.isReady) {
                    val point: PointF? = map_iv_image.viewToSourceCoord(e.x, e.y)
                    if (point != null) {
                        judgePlaceX(point)
                    }
                    LogUtils.d("placePoint", "" + point?.x + " " + point?.y)
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
            for (building in placeX.buildingRectList!!) {
                if ((pointF.x <= building.buildingRight && pointF.x >= building.buildingLeft)) {
                    placeXList.add(placeX)
                }
            }
        }
        judgePlaceY(pointF.y, placeXList)
    }

    /*
     点击地图判断，判断y坐标
     */
    private fun judgePlaceY(y: Float, placeList: ArrayList<PlaceItem>) {
        for (placeY in placeList) {
            for (building in placeY.buildingRectList!!) {
                if (y <= building.buildingBottom && y >= building.buildingTop) {
                    placeY.placeName?.let { replaceFragment(PlaceDetailContentFragment(), placeY.placeId, it) }
                    map_bottom_sheet_content.visibility = View.VISIBLE
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
    }

    private fun changeToActivity(activity: Activity) {
        val intent = Intent(BaseApp.context, activity::class.java)
        startActivity(intent)
    }


    private fun replaceFragment(fragment: Fragment, placeId: Int, placeName: String) {
        val bundle = Bundle()
        bundle.putString("placeId", placeId.toString())
        bundle.putString("placeName", placeName)
        fragment.arguments = bundle
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.map_bottom_sheet_content, fragment)
        transaction.commit()
    }
}
