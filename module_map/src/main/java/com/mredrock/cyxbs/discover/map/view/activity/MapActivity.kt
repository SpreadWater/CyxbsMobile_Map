package com.mredrock.cyxbs.discover.map.view.activity


import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
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
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.davemorrissey.labs.subscaleview.ImageSource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DISCOVER_MAP
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.R.layout.map_activity_map
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.bean.PlaceBasicData
import com.mredrock.cyxbs.discover.map.bean.PlaceItem
import com.mredrock.cyxbs.discover.map.view.adapter.CollectPlaceAdapter
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.discover.map.utils.AddIconImage
import com.mredrock.cyxbs.discover.map.utils.Toast
import com.mredrock.cyxbs.discover.map.view.fragment.PlaceDetailContentFragment
import kotlinx.android.synthetic.main.map_activity_map.*
import kotlinx.android.synthetic.main.map_activity_map.map_iv_image
import kotlinx.android.synthetic.main.map_fragment_collect_place.view.*
import kotlin.collections.ArrayList


@Route(path = DISCOVER_MAP)
class MapActivity : BaseViewModelActivity<MapViewModel>() {
    private val hotWord: String? = null
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
    private var dialog: Dialog? = null
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
        dialog = ProgressDialog(this)
        if (dialog != null) {
            (dialog as ProgressDialog).setMessage("加载中...")
            (dialog as ProgressDialog).show()
        }
        initAddViewToIcon()
        initGetPlaceItemData()
        initTabCategory()
        initBottomSheetBehavior()
    }

    private fun initGetPlaceItemData() {
        viewModel.getPlaceData()
        viewModel.placeBasicData.observe(this, Observer<PlaceBasicData> {
            it?.run {
                if (hotWord != null)
                    map_et_search.setText("  大家都在搜：$hotWord")
                if (placeList != null) {
                    initMapView(placeList as ArrayList<PlaceItem>)
                    dialog?.dismiss()
                }
            }
        })
    }

    private fun initRv(popupView: View) {
        val titleList = ArrayList<String>()
        for (title in viewModel.titles) {
            titleList.add(title)
            titleList.add(title)
        }
        if (titleList.isEmpty()) {
            Toast.toast("啊哦，你还没有收藏地点", Gravity.BOTTOM, 0, 100)
        }
        val collectPlaceAdapter = CollectPlaceAdapter(titleList)
        popupView.map_rv_collect_place.apply {
            layoutManager = LinearLayoutManager(BaseApp.context)
            addItemDecoration(DividerItemDecoration(BaseApp.context, DividerItemDecoration.VERTICAL))
            adapter = collectPlaceAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        initCompass()
    }

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
        AddIconImage.setImageViewToButton(R.drawable.map_ic_collect_list, map_btn_collect_place, 2)
        AddIconImage.setImageViewToButton(R.drawable.map_ic_search_before, map_et_search, 0)

    }

    private fun initBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(map_bottom_sheet_content)
        replaceFragment(PlaceDetailContentFragment())
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
        map_et_search.setOnClickListener {
            changeToActivity(SearchActivity())
        }
    }

    private fun initPopupWindow(view: View?) {
        if (view != null) {
            val popupView = LayoutInflater.from(BaseApp.context).inflate(R.layout.map_fragment_collect_place, null, false);
            val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            popupWindow.animationStyle = R.style.map_anim_popwindow //设置加载动画
            popupWindow.isTouchable = true
            popupWindow.setTouchInterceptor(View.OnTouchListener { v, event ->
                false
            })
            popupWindow.showAsDropDown(view, -100, 10)
            initRv(popupView)
        }
    }

    private fun initTabCategory() {
        map_btn_collect_place.setOnClickListener { view ->
            initPopupWindow(view)
        }
        map_tl_category.setTitle(viewModel.titles)
    }

    private fun initMapView(placelist: ArrayList<PlaceItem>) {
        placeItemList = placelist
        map_iv_image.setImage(ImageSource.resource(R.drawable.map_ic_background))
        map_iv_image.clearPointList()
        map_iv_image.setLocation(0.5f, PointF(placelist[28].placeCenterX, placelist[28].placeCenterY))
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (map_iv_image.isReady) {
                    val point: PointF? = map_iv_image.viewToSourceCoord(e.x, e.y)
                    if (point != null) {
                        judgePlaceX(point)
                    }
                    LogUtils.d("placePoint", "" + point?.x + " " + point?.y);
                }
                return true
            }
        })
        map_iv_image.setOnTouchListener { view, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }
    }

    private fun judgePlaceX(pointF: PointF) {
        placeXList.clear()
        for (placeX in placeItemList) {
            if ((pointF.x <= placeX.buildingRectList!![0].buildingRight && pointF.x >= placeX.buildingRectList!![0].buildingLeft)) {
                placeXList.add(placeX)
            }
        }
        judgePlaceY(pointF.y, placeXList)
    }

    private fun judgePlaceY(y: Float, placeList: ArrayList<PlaceItem>) {
        for (placeY in placeList) {
            if (y <= placeY.buildingRectList!![0].buildingBottom && y >= placeY.buildingRectList!![0].buildingTop) {
                android.widget.Toast.makeText(BaseApp.context, placeY?.placeName, android.widget.Toast.LENGTH_SHORT).show()
                replaceFragment(PlaceDetailContentFragment())
                map_bottom_sheet_content.visibility = View.VISIBLE
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun changeToActivity(activity: Activity) {
        val intent = Intent(BaseApp.context, activity::class.java)
        startActivity(intent)
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.map_bottom_sheet_content, fragment)
        transaction.commit()
    }
}
