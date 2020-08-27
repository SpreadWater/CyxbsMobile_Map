package com.mredrock.cyxbs.discover.map.view.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.net.Uri
import android.os.Environment
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.IconBean
import com.mredrock.cyxbs.discover.map.utils.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.displayMetrics
import java.io.File
import kotlin.math.sqrt


/**
 * @Author: 徐国林
 * @ClassName: MyLayout
 * @Description:
 * @Date: 2020/8/27 11:01
 */
class MyMapLayout : FrameLayout, View.OnClickListener {
    companion object {
        const val FOCUS_ANIMATION_DURATION = 800L
    }

    private val path = Environment.getExternalStorageDirectory().absolutePath + "/cquptmap/map.jpg"

    private var url: String? = null
    var isLock = false

    /** Sub-samplingScaleImageView第三方控件 */
    private val subsamplingScaleImageView = SubsamplingScaleImageView(context)

    /**开始聚焦的id*/
    private var openSiteId = "-1"

    /** 标签array list */
    private val iconList = mutableListOf<ImageView>()

    private var onIconClickListener: OnIconClickListener? = null

    private var onPlaceClickListener: OnPlaceClickListener? = null

    private var onNoPlaceClickListener: OnNoPlaceClickListener? = null

    private var onCloseFinishListener: OnCloseFinishListener? = null

    private var onShowFinishListener: OnShowFinishListener? = null

    private var onUrlGetListener: OnUrlGetListener? = null

    /**
     *下面四个为继承FrameLayout的构造器方法
     */
    constructor(c: Context) :
            this(c, null) {
    }

    constructor(context: Context?, attrs: AttributeSet?) :
            this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context!!, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    /**
     *初始化
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        val rootParams =
                LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        /** 真实dpi<400的手机会非常卡，需要适配*/
        subsamplingScaleImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)

        /** 计算真实的dpi*/
        val xdpi: Float = context.displayMetrics.xdpi
        val ydpi: Float = context.displayMetrics.ydpi
        val width: Int = context.displayMetrics.widthPixels
        val height: Int = context.displayMetrics.heightPixels

        val width2 = width / xdpi * (width / xdpi)
        val height2 = height / ydpi * (height / ydpi)
        val myRealDensity = sqrt(width2 + height2)

        val realDpi = sqrt((((width * width) + (height * height)).toDouble())) / myRealDensity

        /** 适配dpi<400的机型*/
        if (realDpi < 400) {
            subsamplingScaleImageView.setMinimumTileDpi(300)
        }

        subsamplingScaleImageView.setDoubleTapZoomScale(0.5f)


        /**
         * 监听是否获得url
         * url = "loadFromLocal"，先下载地图到本地在加载本地地图
         * 直接下载地图到path
         */
        setOnUrlGetListener(object : OnUrlGetListener {
            override fun onUrlGet() {

                when (url) {
                    "loadFromLocal" -> {
                        GlobalScope.launch(Dispatchers.Main) {
                            subsamplingScaleImageView.setImage(ImageSource.uri(Uri.fromFile(File(path))))
                        }
                    }
                }
            }
        })

        addView(subsamplingScaleImageView, rootParams)

        /**
         * 地图加载监听
         */
        subsamplingScaleImageView.setOnImageEventListener(
                object :
                        SubsamplingScaleImageView.OnImageEventListener {
                    /**
                     * 地图加载完毕
                     */
                    override fun onImageLoaded() {

                        if (openSiteId != "-1") {
                            iconList.forEach { icon ->
                                val iconBean = icon.tag as IconBean
                                if (iconBean.placeId.toString() == openSiteId) {
                                    subsamplingScaleImageView.animateScaleAndCenter(0.5f, PointF(iconBean.placeX, iconBean.placeY))
                                            ?.withDuration(FOCUS_ANIMATION_DURATION)
                                            ?.withInterruptible(true)?.start()
                                    return@forEach
                                }
                            }
                        }


                    }

                    /**
                     * 地图准备就绪，下一步执行绘制
                     * 务必在此方法后再addView(icon,layoutParams)
                     */
                    override fun onReady() {
                        iconList.forEach { icon ->
                            val layoutParams = LayoutParams(
                                    context.dp2px(45f),
                                    context.dp2px(48f)
                            )

                            /**
                             * 若icon有parent，则先移除再添加
                             */
                            val p = icon.parent as ViewGroup?
                            p?.removeView(icon)
                            addView(icon, layoutParams)
                        }


                    }

                    override fun onTileLoadError(e: Exception?) {

                    }

                    override fun onPreviewReleased() {

                    }

                    override fun onImageLoadError(e: Exception?) {

                    }

                    override fun onPreviewLoadError(e: Exception?) {

                    }

                })
        /**点击的位置 */
        var clickPoint = PointF(0f, 0f)

        /**通过此方法获得点击的位置 */
        val gestureDetector =
                GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                        if (subsamplingScaleImageView.isReady) {
                            clickPoint = subsamplingScaleImageView.viewToSourceCoord(e.x, e.y)!!
                        }
                        return true
                    }
                })
        /**监听点击事件 */
        subsamplingScaleImageView.setOnClickListener {
            var count = 0
            if (isLock) {
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.toast(R.string.map_toast_lock)
                }
                return@setOnClickListener
            }
            iconList.forEach { icon ->
                val iconBean = icon.tag as IconBean
                if ((clickPoint.x > iconBean.buildingLeft
                                && clickPoint.x < iconBean.buildingRight
                                && clickPoint.y > iconBean.buildingTop
                                && clickPoint.y < iconBean.buildingBottom) ||
                        (clickPoint.x > iconBean.tagLeft
                                && clickPoint.x < iconBean.tagRight
                                && clickPoint.y > iconBean.tagTop
                                && clickPoint.y < iconBean.tagBottom)) {
                    subsamplingScaleImageView.animateScaleAndCenter(
                            0.5f,
                            iconBean.placeY?.let { it1 -> iconBean.placeX?.let { it2 -> PointF(it2, it1) } }
                    )?.withDuration(FOCUS_ANIMATION_DURATION)
                            ?.withInterruptible(true)?.start()
                    showIcon(icon)
                    onPlaceClickListener?.onPlaceClick(icon)
                } else {
                    count++
                    closeIcon(icon)
                    if (count == iconList.size)
                        onNoPlaceClickListener?.onNoPlaceClick()
                }

            }

        }

        /**监听触摸事件 */
        subsamplingScaleImageView.setOnTouchListener { v, event ->
            return@setOnTouchListener gestureDetector.onTouchEvent(event)
        }

        /**监听移动变化，保持icon不变 */
        subsamplingScaleImageView.setOnStateChangedListener(
                object :
                        SubsamplingScaleImageView.OnStateChangedListener {
                    override fun onCenterChanged(newCenter: PointF?, origin: Int) {
                        iconList.forEach { icon ->
                            val iconBean = icon.tag as IconBean
                            val screenPoint =
                                    subsamplingScaleImageView.sourceToViewCoord(iconBean.placeX, iconBean.placeY)
                            if (screenPoint != null) {
                                icon.x = screenPoint.x - context.dp2px(45f) / 2
                                icon.y = screenPoint.y - context.dp2px(48f)
                            }
                        }

                    }

                    override fun onScaleChanged(newScale: Float, origin: Int) {
                    }

                })

    }

    /**
     * 添加一个标签
     * bean：要添加的标签bean类
     */
    fun addIcon(bean: IconBean) {
        val icon = ImageView(context)
        icon.setImageResource(R.drawable.map_ic_label)
        icon.tag = bean
        val screenPoint = subsamplingScaleImageView.sourceToViewCoord(bean.placeX, bean.placeY)
        if (screenPoint != null) {
            icon.x = screenPoint.x - context.dp2px(45f) / 2
            icon.y = screenPoint.y - context.dp2px(48f)
        }
        icon.setOnClickListener(this)
        icon.gone()
        iconList.add(icon)
        if (subsamplingScaleImageView.isReady) {
            val layoutParams = LayoutParams(
                    context.dp2px(45f),
                    context.dp2px(48f)
            )
            addView(icon, layoutParams)
        }

    }

    /**
     * 添加多个标签
     * beans：要添加的标签bean类list
     */
    fun addSomeIcons(beans: List<IconBean>) {
        beans.forEach { bean ->
            addIcon(bean)
        }

    }


    /**
     * 关闭一个标签
     * icon：要关闭的标签
     */
    fun closeIcon(icon: ImageView) {
        val animator = ValueAnimator.ofFloat(1f, 1.5f, 0f, 0.5f, 0f)
        animator.duration = 300
        animator.addUpdateListener {
            val currentValue: Float = it.animatedValue as Float
            icon.scaleX = currentValue
            icon.scaleY = currentValue
            if (currentValue == 0f) {
                icon.gone()
            }
        }
        animator.start()
    }

    /**
     * 展示一个标签
     * icon：要展示的标签
     */
    private fun showIcon(icon: ImageView) {
        icon.visible()
        val animator = ValueAnimator.ofFloat(0f, 1.2f, 0.8f, 1f)
        animator.duration = 300
        animator.addUpdateListener {
            val currentValue: Float = it.animatedValue as Float
            icon.scaleX = currentValue
            icon.scaleY = currentValue
        }
        animator.start()
    }

    /**
     *关闭所有的标签
     */
    fun closeAllIcon() {
        var delayTime: Long = 0
        val closeList = mutableListOf<ImageView>()
        iconList.forEach { icon ->
            if (icon.visibility == View.VISIBLE) {
                closeList.add(icon)
            }
        }
        closeList.forEach { icon ->
            val animator = ValueAnimator.ofFloat(1f, 1.5f, 0f, 0.5f, 0f)
            animator.duration = 300
            animator.addUpdateListener {
                val currentValue: Float = it.animatedValue as Float
                icon.scaleX = currentValue
                icon.scaleY = currentValue
                if (currentValue == 0f) {
                    icon.gone()
                }
            }
            animator.startDelay = delayTime
            animator.start()
            delayTime += if (closeList.size <= 5) {
                100
            } else {
                50
            }
        }
        android.os.Handler().postDelayed({
            onCloseFinishListener?.onCloseFinish()
        }, delayTime + 300)

    }

    /**
     *展示所有的标签
     */
    fun showAllIcon() {
        iconList.forEach { icon ->
            icon.visibility = View.VISIBLE
            val animator = ValueAnimator.ofFloat(0f, 1.2f, 0.8f, 1f)
            animator.duration = 300
            animator.addUpdateListener {
                val currentValue: Float = it.animatedValue as Float
                icon.scaleX = currentValue
                icon.scaleY = currentValue
            }
            animator.start()
        }
    }

    /**
     * 展示指定范围的标签
     * start：起始位置
     * end：结束位置
     */
    private fun showSomeIcons(start: Int, end: Int) {
        if (start >= 0 && end <= iconList.size) {
            for (i in start..end) {
                val icon = iconList[i]
                icon.visibility = View.VISIBLE
                val animator = ValueAnimator.ofFloat(0f, 1.2f, 0.8f, 1f)
                animator.duration = 300
                animator.addUpdateListener {
                    val currentValue: Float = it.animatedValue as Float
                    icon.scaleX = currentValue
                    icon.scaleY = currentValue
                }
                animator.start()
            }

        }
    }


    /**
     * 放大并平移到某点
     */
    fun focusToPoint(sx: Float, sy: Float) {
        subsamplingScaleImageView.animateScaleAndCenter(
                0.5f,
                PointF(sx, sy)
        )?.withDuration(FOCUS_ANIMATION_DURATION)
                ?.withInterruptible(true)?.start()
    }

    override fun onClick(v: View) {
        onIconClickListener?.onIconClick(v)
    }

    interface OnIconClickListener {
        fun onIconClick(v: View)
    }

    /**
     * 根据id放大并平移到某点
     */
    fun focusToPoint(id: String) {
        iconList.forEach { bean ->
            val iconBean = bean.tag as IconBean
            if (iconBean.placeId.toString() == id) {
                subsamplingScaleImageView.animateScaleAndCenter(
                        0.5f,
                        PointF(iconBean.placeX, iconBean.placeY)
                )?.withDuration(FOCUS_ANIMATION_DURATION)
                        ?.withInterruptible(true)?.start()
                return
            }
        }

    }

    /**
     * public的方法，传入icon的id就可以展示此icon
     */
    fun showIcon(id: String) {
        iconList.forEach {
            val bean = it.tag as IconBean
            val beanId = bean.placeId.toString()
            if (id == beanId) {
                showIcon(it)
                subsamplingScaleImageView.animateScaleAndCenter(0.5f, PointF(bean.placeX, bean.placeY))
                        ?.withDuration(FOCUS_ANIMATION_DURATION)
                        ?.withInterruptible(true)?.start()
                return
            }
        }
    }

    /**
     * public的方法，传入icon的id就可以展示此icon,无地图缩放
     */
    fun showIconWithoutAnim(id: String) {
        iconList.forEach {
            val bean = it.tag as IconBean
            val beanId = bean.placeId.toString()
            if (id == beanId) {
                showIcon(it)
                return
            }
        }
    }

    /**
     * 根据多个id展示多个icon
     */
    fun showSomeIcons(ids: ArrayList<Int>) {
        subsamplingScaleImageView.animateScaleAndCenter(0f, subsamplingScaleImageView.center)
                ?.withDuration(FOCUS_ANIMATION_DURATION)
                ?.withInterruptible(true)?.start()
        val showList = mutableListOf<ImageView>()
        ids.forEach { id ->
            for (i in 0 until iconList.size) {
                val iconBean = iconList[i].tag as IconBean
                if (iconBean.placeId == id) {
                    showList.add(iconList[i])
                    break
                }
            }
        }
        var delayTime: Long = 0
        showList.forEach { icon ->
            android.os.Handler().postDelayed({
                icon.visible()
            }, delayTime)
            val animator = ValueAnimator.ofFloat(0f, 1.2f, 0.8f, 1f)
            animator.duration = 300
            animator.addUpdateListener {
                val currentValue: Float = it.animatedValue as Float
                icon.scaleX = currentValue
                icon.scaleY = currentValue
            }
            animator.startDelay = delayTime
            animator.start()

            delayTime += if (showList.size <= 5) {
                100
            } else {
                50
            }
        }
        android.os.Handler().postDelayed({
            onShowFinishListener?.onShowFinish()
        }, delayTime + 300)
    }


    fun setUrl(url: String) {
        this.url = url
        onUrlGetListener?.onUrlGet()
    }

    fun setIsLock(lock: Boolean) {
        this.isLock = lock
        if (lock) {
            val center = subsamplingScaleImageView.center
            val scale = subsamplingScaleImageView.scale
            subsamplingScaleImageView.isPanEnabled = false
            subsamplingScaleImageView.isZoomEnabled = false
            subsamplingScaleImageView.setScaleAndCenter(scale, center)
        } else {
            subsamplingScaleImageView.isPanEnabled = true
            subsamplingScaleImageView.isZoomEnabled = true
        }
    }

    /**
     * 地图点击回调
     */
    interface OnPlaceClickListener {
        fun onPlaceClick(v: View)
    }

    /**
     * 点击非建筑地区回调
     */
    interface OnNoPlaceClickListener {
        fun onNoPlaceClick()
    }

    /**
     * 关闭动画结束回调
     */
    interface OnCloseFinishListener {
        fun onCloseFinish()
    }

    /**
     * 展示动画结束回调
     */
    interface OnShowFinishListener {
        fun onShowFinish()
    }

    /**
     * 获取url回调
     */
    interface OnUrlGetListener {
        fun onUrlGet()
    }

    fun setMyOnIconClickListener(onIconClickListener: OnIconClickListener) {
        this.onIconClickListener = onIconClickListener
    }

    fun setMyOnPlaceClickListener(onPlaceClickListener: OnPlaceClickListener) {
        this.onPlaceClickListener = onPlaceClickListener
    }

    fun setMyOnNoPlaceClickListener(onNoPlaceClickListener: OnNoPlaceClickListener) {
        this.onNoPlaceClickListener = onNoPlaceClickListener
    }

    fun setOnCloseFinishListener(onCloseFinishListener: OnCloseFinishListener) {
        this.onCloseFinishListener = onCloseFinishListener
    }

    fun setOnShowFinishListener(onShowFinishListener: OnShowFinishListener) {
        this.onShowFinishListener = onShowFinishListener
    }

    private fun setOnUrlGetListener(onUrlGetListener: OnUrlGetListener) {
        this.onUrlGetListener = onUrlGetListener
    }

    fun removeMyViews() {
        this.removeAllViews()
    }

    fun setOpenSiteId(id: String) {
        this.openSiteId = id
    }
}