package com.mredrock.cyxbs.discover.map.view.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.map.R
import kotlinx.android.synthetic.main.map_activity_map.*


class PinView(context: Context?, attr: AttributeSet?) : SubsamplingScaleImageView(context, attr) {
    private val paint: Paint = Paint()
    private var pin: Bitmap? = null
    private val point = PointF()
    private var pointList = ArrayList<PointF>()
    private var pinVisibilityList: MutableList<Boolean> = ArrayList()
    private var pinPositionList: MutableList<PointF> = ArrayList()
    private var pinBitmapList: MutableList<Bitmap> = ArrayList()

    constructor(context: Context?) : this(context, null) {}

    fun setPin(sPin: PointF?) {
        if (sPin != null) {
            pin?.let { pinBitmapList.add(it) }
            pinVisibilityList.add(true)
            pinPositionList.add(PointF(0f, 0f))
            pointList.add(sPin)
        }
        invalidate()
    }

    fun stopScale() {
        setScaleAndCenter(0f, center)
    }

    fun addPointF(pointList: ArrayList<PointF>) {
        for (point in pointList)
            setPin(point)
    }

    fun removePins() {
        pinBitmapList.clear()
        pinVisibilityList.clear()
        pinPositionList.clear()
        pointList.clear()
        invalidate()
    }

    //初始化图标
    private fun initialise() {
        pin = BitmapFactory.decodeResource(this.resources, R.drawable.map_ic_label)
        pin = Bitmap.createScaledBitmap(pin, 60, 80, true)
    }

    //在地图上画点
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isReady) {
            return
        }
        // Don't draw pin before image is ready so it doesn't move around during setup.
        paint.isAntiAlias = true
        var vPin: PointF = PointF()
        for (i: Int in pinBitmapList.indices) {
            sourceToViewCoord(pointList[i], vPin)
            point.set(pinBitmapList[i].width.toFloat(), pinBitmapList[i].height.toFloat())
            val vX = vPin.x - pinBitmapList[i].width / 2
            val vY = vPin.y - pinBitmapList[i].height
            pinPositionList[i].set(pointList[i].x - point.x / 2, pointList[i].y - point.y)
            if (pinVisibilityList[i]) {
                canvas.drawBitmap(pinBitmapList[i], vX, vY, paint)
            }
        }
    }

    //定位具体位置并放缩
    fun setLocation(pointF: PointF) {
        setScaleAndCenter(0.5f,pointF)
        setPin(pointF)
    }

    init {
        initialise()
    }
}