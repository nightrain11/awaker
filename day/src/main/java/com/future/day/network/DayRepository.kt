package com.future.day.network

import com.future.common.CommonUtils
import com.future.day.db.DayDatabase
import com.future.day.db.entity.DayNew
import com.future.day.helper.DayNewHelper
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class DayRepository {

    companion object {

        private var INSTANCE: DayRepository? = null

        @JvmStatic
        fun get(): DayRepository = INSTANCE ?: synchronized(DayRepository::class) {
            INSTANCE ?: DayRepository().also {
                INSTANCE = it
            }
        }
    }

    private val dayDatabase: DayDatabase by lazy {
        DayDatabase.get(CommonUtils.get().getContext())
    }
    private val dayApi: DayApi by lazy {
        DayClient.get()
    }

    fun getDayNewList(page: Int, ver: String, appVer: String): Single<List<DayNew>> {
        return dayApi.getDayNewList(page, ver, appVer)
            .subscribeOn(Schedulers.io())
            .map { list ->
                val dayNewList = DayNewHelper.toDayNewList(list)
                insertDayNewList(dayNewList)
                dayNewList
            }
    }

    fun loadDayNewList(): Flowable<List<DayNew>> {
        return dayDatabase.dayNewDao().loadDayNewList()
    }

    fun loadDayNewList(tagKey: String): Flowable<List<DayNew>> {
        return dayDatabase.dayNewDao().loadDayNewList(tagKey)
    }

    private fun insertDayNewList(dayNewList: List<DayNew>) {
        return dayDatabase.dayNewDao().insertDayNewList(dayNewList)
    }
}