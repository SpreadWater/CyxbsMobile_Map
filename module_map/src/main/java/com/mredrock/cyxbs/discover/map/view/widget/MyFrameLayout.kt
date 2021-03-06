package com.mredrock.cyxbs.discover.map.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

/*
    解决拦截事件的冲突：bottom与pinview
 */
class MyFrameLayout(context: Context?, attr: AttributeSet?) : FrameLayout(context, attr)
{
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }
}