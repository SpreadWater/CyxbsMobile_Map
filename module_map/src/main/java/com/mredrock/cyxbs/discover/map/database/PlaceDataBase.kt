package com.mredrock.cyxbs.discover.map.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.bean.PlaceItem

/**
 * @Author: 徐国林
 * @ClassName: PlaceDataBase
 * @Description:
 * @Date: 2020/8/20 15:08
 */

@Database(version = 1, entities = [PlaceItem::class])
abstract class PlaceDatabase : RoomDatabase() {
    abstract fun getPlaceDao(): PlaceDao

    companion object {
        private var instance: PlaceDatabase? = null

        @Synchronized
        fun getDatabase(): PlaceDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(BaseApp.context.applicationContext, PlaceDatabase::class.java, "app_place_database")
                    .build().apply {
                        instance = this
                    }
        }
    }
}