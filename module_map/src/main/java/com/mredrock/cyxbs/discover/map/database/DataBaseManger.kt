package com.mredrock.cyxbs.discover.map.database

import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.map.bean.PlaceItem
import com.mredrock.cyxbs.discover.map.config.PlaceData

/**
 * @Author: 徐国林
 * @ClassName: DataBaseManger
 * @Description:
 * @Date: 2020/8/20 16:23
 */
class DataBaseManger {

    companion object {
        private fun loadPlaceDataBase(): PlaceDao {
            return PlaceDatabase.getDatabase().getPlaceDao()
        }

        private fun loadCollectDataBase(): PlaceDao {
            return CollectDataBase.getDatabase().getCollectDao()
        }

        fun saveAllPlaces() {
            loadPlaceDataBase().deleteAllPlaces()
            loadPlaceDataBase().insertAllPlaces(PlaceData.placeBasicData)
        }

        fun getAllPlaces() {
            PlaceData.placeBasicData.clear()
            PlaceData.placeBasicData.addAll(loadPlaceDataBase().queryAllPlaces())
        }

        fun saveAllCollect() {
            loadCollectDataBase().deleteAllPlaces()
            loadCollectDataBase().insertAllPlaces(PlaceData.collectPlace)
        }

        fun getAllCollect() {
            PlaceData.collectPlace.clear()
            PlaceData.collectPlace.addAll(loadCollectDataBase().queryAllPlaces())
        }

        fun deleteCollectByPlaceId(id: Int) {
            loadCollectDataBase().deletePlacesById(id)
        }

        fun insertCollectByPlaceId(placeItem: PlaceItem) {
            loadCollectDataBase().insertPlace(placeItem)
        }
    }
}