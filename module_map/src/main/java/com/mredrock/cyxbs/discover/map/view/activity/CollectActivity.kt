package com.mredrock.cyxbs.discover.map.view.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.view.adapter.AutoWrapAdapter
import com.mredrock.cyxbs.discover.map.view.widget.CollectDialog
import com.mredrock.cyxbs.discover.map.viewmodel.CollectPlaceViewModel
import kotlinx.android.synthetic.main.map_activity_collect.*


class CollectActivity : BaseViewModelActivity<CollectPlaceViewModel>() {
    override val isFragmentActivity: Boolean
        get() = false
    override val viewModelClass = CollectPlaceViewModel::class.java

    private var signatureCs: CharSequence? = null

    val mlist = ArrayList<AutoWrapAdapter.recommend>()

    val isCollect = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity_collect)
        initdata()
        initisCollect()
        initEvent()
    }

    private fun showDialog() = Unit

    fun initdata() {
        repeat(2) {
            mlist.add(AutoWrapAdapter.recommend("我的宿舍"))
            mlist.add(AutoWrapAdapter.recommend("隐藏的角落"))
            mlist.add(AutoWrapAdapter.recommend("宝藏地点"))
        }
    }

    fun isCollect() {
        map_tv_collect_cancel.visibility = View.VISIBLE
        map_tv_collect_place_hint.text = "将该地点收藏改为"
    }

    fun isNotCollect() {
        map_tv_collect_cancel.visibility = View.GONE
    }

    //如果收藏了就更改参数显示问题
    fun initisCollect() {
        if (isCollect) {
            isCollect()

        } else {
            isNotCollect()
        }
    }

    private fun initEvent() {

        map_tv_collect_confirm.setOnClickListener {
            if (isCollect) {
                Toast.makeText(this, "确认修改收藏", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "确认收藏发起网络请求", Toast.LENGTH_SHORT).show()
            }
        }

        map_tv_collect_cancel.setOnClickListener {
            Toast.makeText(this, "取消收藏返回详情", Toast.LENGTH_SHORT).show()
            finish()
        }

        map_tv_collect_cancel_place.setOnClickListener {
            showDialog()
        }
        map_aw_collect_recommend.adapter = AutoWrapAdapter(mlist) { text ->
            map_et_collect_collectname.setText(text)
            map_et_collect_collectname.setSelection(map_et_collect_collectname.text!!.length)
        }
        map_et_collect_collectname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                map_tv_collect_namenumber.text = "${signatureCs?.length}/8"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s != null) {
                    signatureCs = s
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s.toString().contains(" ") -> {
                        val str = s.toString().split(" ")
                        val sb = StringBuffer()
                        for (i in str.indices) {
                            sb.append(str[i])
                        }
                        map_et_collect_collectname.setText(sb.toString())
                        map_et_collect_collectname.setSelection(start)
                    }
                    s.toString().contains("\n") -> {
                        val str = s.toString().split("\n")
                        val sb = StringBuffer()
                        for (i in str.indices) {
                            sb.append(str[i])
                        }
                        map_et_collect_collectname.setText(sb.toString())
                        map_et_collect_collectname.setSelection(start)
                    }
                    s != null -> {
                        signatureCs = s
                    }
                }
            }
        })
    }

    companion object {
        private fun showDialog(collectActivity: CollectActivity) {

            val dialog = CollectDialog(collectActivity)
            dialog.setListener(object : CollectDialog.OnClickListener {
                override fun onCancel() {
                    dialog.dismiss()
                }

                override fun onConfirm() {
                    Toast.makeText(collectActivity, "发送取消收藏网络请求…………", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            })
            dialog.show()
        }
    }
}