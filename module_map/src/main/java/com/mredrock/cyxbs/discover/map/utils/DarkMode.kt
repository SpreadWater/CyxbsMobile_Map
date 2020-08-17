package com.mredrock.cyxbs.discover.map.utils

import android.content.Context
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor

/**
 *@date 2020-8-17
 *@author zhangsan
 *@description
 */
const val IS_OPEN_DARK_MODE = "isOpenDarkMode"  // 是否开启了黑夜模式
const val IS_DARK_MODE_CHANGED="isDarkModeChanged"//判断是不是黑夜模式的切换
var Context.isDarkMode: Boolean
    get() = defaultSharedPreferences.getBoolean(IS_OPEN_DARK_MODE, false)
    set(value) {
        defaultSharedPreferences.editor {
            putBoolean(IS_OPEN_DARK_MODE, value)
        }
    }

var Fragment.isDarkMode: Boolean
    get() = context?.isDarkMode ?: false
    set(value) {
        context?.isDarkMode = value
    }

var Context.isDarkModeChanged: Boolean
    get() = defaultSharedPreferences.getBoolean(IS_DARK_MODE_CHANGED, false)
    set(value) {
        defaultSharedPreferences.editor {
            putBoolean(IS_DARK_MODE_CHANGED, value)
        }
    }