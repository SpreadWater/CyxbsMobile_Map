package com.mredrock.cyxbs.discover.map.view.activity


import android.app.Activity
import android.app.Dialog
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
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.discover.map.utils.AddIconImage
import com.mredrock.cyxbs.discover.map.utils.Toast
import com.mredrock.cyxbs.discover.map.view.fragment.PlaceDetailContentFragment
import kotlinx.android.synthetic.main.map_activity_map.*
import kotlinx.android.synthetic.main.map_activity_map.map_iv_image
import kotlin.collections.ArrayList


@Route(path = DISCOVER_MAP)
class MapActivity : BaseViewModelActivity<MapViewModel>() {
    private val placeXList = ArrayList<PlaceItem>()
    private var placeItemList = ArrayList<PlaceItem>()
    override val isFragmentActivity: Boolean
        get() = false
    override val viewModelClass: Class<MapViewModel>
        get() = MapViewModel::class.java
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private var sensorManager: SensorManager? = null
    private var magnetic: Sensor? = null
    private var accelerometer: Sensor? = null
    private var hot_Word: String? = null
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
        GetHotWord()
        GetPlaceItemDataFromLocal()
        initTabCategory()
        initBottomSheetBehavior()
        initIconClick()
    }


    /*
    得到hotword
     */
    fun GetHotWord() {
        viewModel.getHotWord()
        viewModel.hotWord.observe(this, Observer {
            map_et_search.setText("  大家都在搜：${it}")
        })
    }

    /*
        从本地拿到数据
     */
    private fun GetPlaceItemDataFromLocal() {
//        var count = 1
//        while (count != 126) {
//            if (HistoryPlaceDao.getSavedPlace(count) != null) {
//                HistoryPlaceDao.getSavedPlace(count)?.let { placeItemList.add(it) }
//                count++
//            }
//        }
//        if (placeItemList == null)
        GetPlaceItemDataFromNetwork()
    }

    /*
        从网络拿到地点基础数据
     */
    private fun GetPlaceItemDataFromNetwork() {
        viewModel.getPlaceData()
        viewModel.placeBasicData.observe(this, Observer<PlaceBasicData> {
            it?.run {
                if (!hotWord.equals("")) {
                    hot_Word = hotWord
                    map_et_search.setText("  大家都在搜：$hotWord")
                } else {
                    map_et_search.setText("  大家都在搜：红岩网校")
                }
                if (placeList != null) {
                    placeItemList = placeList as ArrayList<PlaceItem>
                    for (place in it.placeList!!) {
                        //保存到本地数据库
                        HistoryPlaceDao.savePlace(place)
                    }
                    initMapView(placeItemList)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        initCompass()
    }

    /*
    指南针监听
     */
    private val listener: SensorEventListener = object : SensorEventListener {
        var accelerometerValues = FloatArray(3)
        var magneticValues = FloatArray(3)
        private var startDegree = 0f
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.getType() === Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values.clone()
            } else if (event.sensor.getType() === Sensor.TYPE_MAGNETIC_FIELD) {
                magneticValues = event.values.clone()
            }
            val R = FloatArray(9)
            val values = FloatArray(3)
            SensorManager.getRotationMatrix(R, null, accelerometerValues,
                    magneticValues)
            SensorManager.getOrientation(R, values)
            val endDegree = (Math.toDegrees(values[0].toDouble())).toFloat()
            val animation = RotateAnimation(startDegree,
                    endDegree, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f)
            animation.fillAfter = true // 动画执行完后是否停留在执行完的状态
            map_iv_compass.startAnimation(animation)
            startDegree = endDegree
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(listener)
    }

    /*
    初始化指南针
     */
    private fun initCompass() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?
        magnetic = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager?.registerListener(listener, magnetic,
                SensorManager.SENSOR_DELAY_GAME)
        sensorManager?.registerListener(listener, accelerometer,
                SensorManager.SENSOR_DELAY_GAME)
    }

    private fun initAddViewToIcon() {
        AddIconImage.setImageViewToButton(R.drawable.map_ic_search_before, map_et_search, 0)

    }

    /*
    bottom初始化
     */
    private fun initBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(map_bottom_sheet_content)
        replaceFragment(PlaceDetailContentFragment(), 29)
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
        map_et_search.setOnClickListener {
            changeToActivity(SearchActivity(), placeItemList)
        }
    }

    /*
    tab菜单
     */
    private fun initTabCategory() {
        map_btn_collect_place.setOnClickListener { view ->

        }
        map_tl_category.setTitle(viewModel.getTabLayoutTitles())
        map_tl_category.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                //后端数据存在为空现象问题暂时不启用
//                viewModel.getTypeWordPlaceList(viewModel.getTabLayoutTitles()[tab?.position!! - 1])
//                viewModel.typewordPlaceData.observe(this@MapActivity, Observer {
//                    if (it != null) {
//
//                    }
//                })
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
        if (placeList == null)
            GetPlaceItemDataFromLocal()
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
            if ((pointF.x <= placeX.buildingRectList!![0].buildingRight && pointF.x >= placeX.buildingRectList!![0].buildingLeft)) {
                placeXList.add(placeX)
            }
        }
        judgePlaceY(pointF.y, placeXList)
    }

    /*
     点击地图判断，判断y坐标
     */
    private fun judgePlaceY(y: Float, placeList: ArrayList<PlaceItem>) {
        for (placeY in placeList) {
            if (y <= placeY.buildingRectList!![0].buildingBottom && y >= placeY.buildingRectList!![0].buildingTop) {
                replaceFragment(PlaceDetailContentFragment(), placeY.placeId)
                map_bottom_sheet_content.visibility = View.VISIBLE
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun changeToActivity(activity: Activity) {
        val intent = Intent(BaseApp.context, activity::class.java)
        startActivity(intent)
    }

    private fun changeToActivity(activity: Activity, placelist: ArrayList<PlaceItem>) {
        val intent = Intent(BaseApp.context, activity::class.java)
        intent.putExtra("place_num", placelist.size)
        intent.putExtra("hot_word", hot_Word)
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
