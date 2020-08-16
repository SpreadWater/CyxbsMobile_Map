package com.mredrock.cyxbs.discover.map.view.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.map.R


class PinView(context: Context?, attr: AttributeSet?) : SubsamplingScaleImageView(context, attr) {
    private val paint: Paint = Paint()
    private var pin: Bitmap? = null
    private var pointList = ArrayList<PointF>()

    constructor(context: Context?) : this(context, null) {}

    fun setPin(sPin: PointF?) {
        if (sPin != null) {
            pointList.add(sPin)
        }
        initialise()
        invalidate()
    }

    fun stopScale() {
        setScaleAndCenter(0f, center)
    }

    fun addPointF(pointF: ArrayList<PointF>) {
        pointList.addAll(pointF)
        initialise()
        invalidate()
    }

    fun clearPointList() {
        pointList.clear()
        initialise()
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
        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady) {
            return
        }
        var vPin: PointF = PointF()
        paint.isAntiAlias = true
        for (spin in pointList) {
            sourceToViewCoord(spin, vPin)
            val vX: Float = vPin.x - pin!!.width / 2
            val vY: Float = vPin.y - pin!!.height
            canvas.drawBitmap(pin, vX, vY, paint)
        }
    }

    //定位具体位置并放缩
    fun setLocation(scale: Float, pointF: PointF) {
        setScaleAndCenter(scale, pointF)
        setPin(pointF)
    }

    init {
        initialise()
    }
}