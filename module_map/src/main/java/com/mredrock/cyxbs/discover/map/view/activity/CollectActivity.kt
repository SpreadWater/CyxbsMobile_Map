package com.mredrock.cyxbs.discover.map.view.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.model.dao.CollectDao
import com.mredrock.cyxbs.discover.map.model.dao.HistoryPlaceDao
import com.mredrock.cyxbs.discover.map.view.adapter.AutoWrapAdapter
import com.mredrock.cyxbs.discover.map.view.fragment.PlaceDetailContentFragment
import com.mredrock.cyxbs.discover.map.view.widget.CollectDialog
import com.mredrock.cyxbs.discover.map.viewmodel.CollectPlaceViewModel
import kotlinx.android.synthetic.main.map_activity_collect.*


class CollectActivity : BaseViewModelActivity<CollectPlaceViewModel>() {

    override val isFragmentActivity: Boolean
        get() = false
    override val viewModelClass = CollectPlaceViewModel::class.java

    private var signatureCs: CharSequence? = null

    val mlist = ArrayList<AutoWrapAdapter.recommend>()

    var isCollect = true

    var tag=""

    var mplaceid=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity_collect)
        initdata()
        initisCollect()
        initEvent()
    }

    fun initdata() {
        mplaceid=intent.getIntExtra("PlaceCollect",0)
        isCollect=intent.getBooleanExtra("CollectStatus",false)
        tag=intent.getStringExtra("CollectAtribute")
        //获取点击进来的place
        val placeItem=HistoryPlaceDao.getSavedPlace(mplaceid)

        if (placeItem!=null){
            map_tv_collect_place.text=placeItem.placeName
        }
        if(!tag.equals("")){
            map_tv_collect_place_alias.text=tag
        }
        repeat(2) {
            mlist.add(AutoWrapAdapter.recommend("我的宿舍"))
            mlist.add(AutoWrapAdapter.recommend("隐藏的角落"))
            mlist.add(AutoWrapAdapter.recommend("宝藏地点"))
        }
    }
    fun isCollect() {
        map_tv_collect_cancel_place.visibility = View.VISIBLE
        map_tv_collect_place_hint.text = "将该地点收藏改为"
    }

    fun isNotCollect() {
        map_tv_collect_cancel_place.visibility = View.GONE
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
                viewModel.addCollectPlace(map_et_collect_collectname.text.toString(),mplaceid)
                finish()
            } else {
                if (map_et_collect_collectname.text!!.equals("我的收藏")){
                    viewModel.addCollectPlace(map_et_collect_collectname.text.toString()+"1",mplaceid)
                    //存储当前收藏的状态
                    CollectDao.saveCollectStatus(mplaceid,true)
                }else{
                    viewModel.addCollectPlace(map_et_collect_collectname.text.toString(),mplaceid)
                    //存储当前收藏的状态
                    CollectDao.saveCollectStatus(mplaceid,true)
                }
                finish()
            }
        }

        map_tv_collect_cancel.setOnClickListener {
            finish()
        }

        map_tv_collect_cancel_place.setOnClickListener {
            showDialog(this)
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

        private fun showDialog(collectActivity: CollectActivity) {

            val dialog = CollectDialog(collectActivity)
            dialog.setListener(object : CollectDialog.OnClickListener {
                override fun onCancel() {
                    dialog.dismiss()
                }

                override fun onConfirm() {
                    viewModel.deleteCollectPlace(mplaceid)
                    CollectDao.saveCollectStatus(mplaceid,false)
                    dialog.dismiss()
                }
            })
            dialog.show()
        }
    }