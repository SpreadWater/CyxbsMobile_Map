package com.mredrock.cyxbs.discover.map.view.activity

import android.graphics.PointF
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.PopupWindow
import com.alibaba.android.arouter.facade.annotation.Route
import com.davemorrissey.labs.subscaleview.ImageSource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DISCOVER_MAP
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.R.layout.map_activity_map2
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.discover.map.utils.AddIconImage
import kotlinx.android.synthetic.main.map_activity_map2.*


@Route(path = DISCOVER_MAP)
class MapActivity : BaseViewModelActivity<MapViewModel>() {

    private val titles = listOf("入校报到点", "运动场", "教学楼", "图书馆", "食堂", "快递")
    override val isFragmentActivity: Boolean
        get() = false
    override val viewModelClass: Class<MapViewModel>
        get() = MapViewModel::class.java
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private val pointList = ArrayList<PointF>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(map_activity_map2)
        initAddViewToIcon()
        initMapView()
        initTabCategory()
        initBottomSheetBehavior()
    }

    private fun initAddViewToIcon() {
        AddIconImage.setImageViewToButton(R.drawable.map_ic_my_collect, map_btn_collect_place, 2)
        AddIconImage.setImageViewToButton(R.drawable.map_ic_search, map_et_search, 0)

    }

    private fun initBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(map_bottom_sheet_content)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomSheetBehavior.peekHeight = 350
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
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
            popupWindow.showAsDropDown(view, -200, 10)
        }
    }

    private fun initTabCategory() {
        map_btn_collect_place.setOnClickListener { view ->
            initPopupWindow(view)
        }
        for (title in titles)
            map_tl_category.addTab(map_tl_category.newTab().setText(title))
        map_tl_category.setSelectedTabIndicator(R.drawable.map_ic_tab_indicator)
    }

    private fun initMapView() {
        map_iv_image.setImage(ImageSource.resource(R.drawable.map_ic_background))
        map_iv_image.setLocation(0.5f, PointF(1571f, 8657f))
        map_iv_image.clearPointList()
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (map_iv_image.isReady) {
                    map_bottom_sheet_content.visibility = View.VISIBLE
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    val point: PointF? = map_iv_image.viewToSourceCoord(e.x, e.y)
                    if (point != null) {
                        map_iv_image.setPin(point)
                    }
                    LogUtils.d("placePoint", "" + point?.x + point?.y);
                }
                return true
            }
        })
        map_iv_image.setOnTouchListener { view, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }
    }
}
