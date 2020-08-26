package com.mredrock.cyxbs.discover.map.view.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PointF
import android.net.Uri
import android.os.Environment
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
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
import java.io.File
import java.lang.Exception

/**
 * @Author: 徐国林
 * @ClassName: MapLayout
 * @Description:
 * @Date: 2020/8/23 14:36
 */
class MapLayout : FrameLayout, View.OnClickListener {

    private val path = Environment.getExternalStorageDirectory().absolutePath + "/cquptmap/map.jpg"
    private var url: String? = null
    private val subScaleView = SubsamplingScaleImageView(context)
    private val iconList = mutableListOf<ImageView>()
    private var openId: Int? = -1
    private var onCloseFinishListener: OnCloseFinishListener? = null
    private var onShowFinishListener: OnShowFinishListener? = null
    private var urlListener: OnUrlListener? = null
    private var iconListenter: OnIconClickListener? = null
    private var noPlaceListener: OnNoPlaceClickListener? = null
    private var placeListener: OnPlaceClickListener? = null
    private var isLock: Boolean = false

    companion object {
        const val FOCUS_ANIMATION_DURATION = 800L
    }

    override fun onClick(v: View?) {
        if (v != null) {
            iconListenter?.onIconClick(v)
        }
    }

    constructor(context: Context?, attrs: AttributeSet?) :
            this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context!!, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private fun initView() {

        val rootParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        subScaleView.setDoubleTapZoomScale(0.5f)

        setOnUrlListener(object : OnUrlListener {
            override fun setUrlListener(url: String) {
                when (url) {
                    "loadFromLocal" -> {
                        GlobalScope.launch(Dispatchers.Main) {
                            subScaleView.setImage(ImageSource.uri(Uri.fromFile(File(path))))
                        }
                    }
                }
            }
        })

        addView(subScaleView, rootParams)

        subScaleView.setOnImageEventListener(object : SubsamplingScaleImageView.OnImageEventListener {
            /*
                地图加载完毕，开始绘制
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

            /*
            先监听地图是否加载完毕
             */
            override fun onImageLoaded() {
                if (openId != -1) {
                    iconList.forEach { icon ->
                        val iconBean = icon.tag as IconBean
                        if (iconBean.placeId == openId) {
                            subScaleView.animateScaleAndCenter(0.5f, iconBean.placeY?.let { iconBean.placeX?.let { it1 -> PointF(it1, it) } })
                                    ?.withDuration(FOCUS_ANIMATION_DURATION)
                                    ?.withInterruptible(true)?.start()
                            return@forEach
                        }
                    }
                }
            }

            override fun onPreviewLoadError(e: Exception?) {

            }

            override fun onImageLoadError(e: Exception?) {

            }

            override fun onTileLoadError(e: Exception?) {
            }

            override fun onPreviewReleased() {
            }

        })

        var clickPoint = PointF(0f, 0f)
        val gestureDetector =
                GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                        if (subScaleView.isReady) {
                            clickPoint = subScaleView.viewToSourceCoord(e.x, e.y)!!
                            LogUtils.d("tagtagtag", clickPoint.toString())
                        }
                        return true
                    }
                })

        /**监听点击事件 */
        subScaleView.setOnClickListener {
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
                    LogUtils.d("tagtagname", iconBean.toString())
                    subScaleView.animateScaleAndCenter(
                            0.5f,
                            iconBean.placeY?.let { it1 -> iconBean.placeX?.let { it2 -> PointF(it2, it1) } }
                    )?.withDuration(FOCUS_ANIMATION_DURATION)
                            ?.withInterruptible(true)?.start()
                    showIcon(icon)
                    placeListener?.onPlaceClick(icon)
                } else {
                    count++
                    closeIcon(icon)
                    if (count == iconList.size)
                        noPlaceListener?.onNoPlaceClick()
                }

            }
            /**监听触摸事件 */
            subScaleView.setOnTouchListener { v, event ->
                return@setOnTouchListener gestureDetector.onTouchEvent(event)
            }

            subScaleView.setOnStateChangedListener(object : SubsamplingScaleImageView.OnStateChangedListener {
                override fun onScaleChanged(newScale: Float, origin: Int) {
                }

                override fun onCenterChanged(newCenter: PointF?, origin: Int) {
                    iconList.forEach { icon ->
                        val iconBean = icon.tag as IconBean
                        val point = iconBean.placeY?.let { it1 -> iconBean.placeX?.let { it2 -> subScaleView.viewToSourceCoord(it2, it1) } }
                        if (point != null) {
                            icon.x = point.x - context.dp2px(45f) / 2
                            icon.y = point.y - context.dp2px(48f)
                        }
                    }
                }

            })

        }

    }


    fun addIcon(bean: IconBean) {
        val icon = ImageView(context)
        icon.setImageResource(R.drawable.map_ic_label)
        icon.tag = bean
        val screenPoint = bean.placeX?.let { bean.placeY?.let { it1 -> subScaleView.sourceToViewCoord(it, it1) } }
        if (screenPoint != null) {
            icon.x = screenPoint.x - context.dp2px(45f) / 2
            icon.y = screenPoint.y - context.dp2px(48f)
        }
        icon.setOnClickListener(this)
        icon.gone()
        iconList.add(icon)
        if (subScaleView.isReady) {
            val layoutParams = LayoutParams(
                    context.dp2px(45f),
                    context.dp2px(48f)
            )
            addView(icon, layoutParams)
        }
    }

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

    fun showIcon(icon: ImageView) {
        LogUtils.d("tagtagname", icon.toString())
        icon.visible()
        val animator = ValueAnimator.ofFloat(0f, 1.2f, 0.8f, 1f)
        animator.duration = 300
        animator.addUpdateListener {
            val currentValue = it.animatedValue
            icon.scaleX = currentValue as Float
            icon.scaleY = currentValue as Float
        }
        animator.start()
    }

    fun addSomeIcon(iconList: MutableList<IconBean>) {
        iconList.forEach { icon ->
            addIcon(icon)
        }
    }

    fun closeAllIcons() {
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
     * 根据id放大并平移到某点
     */
    fun focusToPoint(id: Int) {
        iconList.forEach { bean ->
            val iconBean = bean.tag as IconBean
            if (iconBean.placeId == id) {
                subScaleView.animateScaleAndCenter(
                        1f,
                        iconBean.placeY?.let { iconBean.placeX?.let { it1 -> PointF(it1, it) } }
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
                subScaleView.animateScaleAndCenter(1f, bean.placeX?.let { it1 -> bean.placeY?.let { it2 -> PointF(it1, it2) } })
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
    fun showSomeIcons(id: ArrayList<Int>) {
        subScaleView.animateScaleAndCenter(0f, subScaleView.center)
                ?.withDuration(FOCUS_ANIMATION_DURATION)
                ?.withInterruptible(true)?.start()
        val showList = mutableListOf<ImageView>()
        id.forEach { id ->
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

    fun removeALlIcons() {
        this.removeAllViews()
    }

    fun setIsLock(lock: Boolean) {
        this.isLock = lock
        if (lock) {
            val center = subScaleView.center
            val scale = subScaleView.scale
            subScaleView.isPanEnabled = false
            subScaleView.isZoomEnabled = false
            subScaleView.setScaleAndCenter(scale, center)
        } else {
            subScaleView.isPanEnabled = true
            subScaleView.isZoomEnabled = true
        }
    }


    fun setOpenId(openId: Int) {
        this.openId = openId
    }

    fun setUrl(url: String) {
        this.url = url
        urlListener?.setUrlListener(url)
    }

    fun setOnCloseFinishListener(onCloseFinishListener: OnCloseFinishListener) {
        this.onCloseFinishListener = onCloseFinishListener
    }

    fun setOnShowFinishListener(onShowFinishListener: OnShowFinishListener) {
        this.onShowFinishListener = onShowFinishListener
    }

    fun setOnUrlListener(urlListener: OnUrlListener) {
        this.urlListener = urlListener
    }

    fun setOnIconClickListener(onIconClickListener: OnIconClickListener) {
        this.iconListenter = onIconClickListener
    }

    fun setOnPlaceClickListener(onPlaceClickListener: OnPlaceClickListener) {
        this.placeListener = onPlaceClickListener
    }

    fun setOnNoPlaceClickListener(onNoPlaceClickListener: OnNoPlaceClickListener) {
        this.noPlaceListener = onNoPlaceClickListener
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

    interface OnIconClickListener {
        fun onIconClick(icon: View)
    }

    interface OnPlaceClickListener {
        fun onPlaceClick(icon: ImageView)
    }

    interface OnNoPlaceClickListener {
        fun onNoPlaceClick()
    }

    interface OnUrlListener {
        fun setUrlListener(url: String)
    }
}

