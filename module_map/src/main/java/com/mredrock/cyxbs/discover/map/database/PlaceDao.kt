package com.mredrock.cyxbs.discover.map.database

import androidx.room.*
import com.mredrock.cyxbs.discover.map.bean.PlaceItem

/**
 * @Author: 徐国林
 * @ClassName: PlaceDao
 * @Description:
 * @Date: 2020/8/20 15:10
 */

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)//避免重复添加
    fun insertAllPlaces(places: MutableList<PlaceItem>)//一次性添加所有的地址

    @Insert(onConflict = OnConflictStrategy.REPLACE)//避免重复添加
    fun insertPlace(place: PlaceItem)//添加单个地址

    @Query("select * from places")
    fun queryAllPlaces(): List<PlaceItem>//获取全部地址

    @Query("delete from places")
    fun deleteAllPlaces()//删除全部地址

    @Query("delete from places where placeId = :id")
    fun deletePlacesById(id: Int)//根据ID删除地点

    @Update
    fun updatePlace(place: PlaceItem)//更新地点数据
}