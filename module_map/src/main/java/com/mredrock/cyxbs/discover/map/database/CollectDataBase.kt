package com.mredrock.cyxbs.discover.map.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.bean.PlaceItem

/**
 * @Author: 徐国林
 * @ClassName: CollectDataBase
 * @Description:
 * @Date: 2020/8/20 16:28
 */

@Database(version = 2, entities = [PlaceItem::class])
abstract class CollectDataBase : RoomDatabase() {
    abstract fun getCollectDao(): PlaceDao

    companion object {
        private var instance: CollectDataBase? = null

        @Synchronized
        fun getDatabase(): CollectDataBase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(BaseApp.context.applicationContext, CollectDataBase::class.java, "app_collect_database")
                    .build().apply {
                        instance = this
                    }
        }
    }
}