package com.swx.dongzhou.HistoryDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.swx.dongzhou.Util.QRCodeType


@Dao
interface HistoryDao {

    @Query("select * from history where isFavorite=true")
    fun selectIsFavorite():List<History>

    @Query("select * from history order by createdAt desc")
    fun selectAll(): List<History>

    @Query("select * from history where type = :type ")
    fun selectByType(type: QRCodeType):List<History>

    @Delete
    fun delect(history: History)

    @Insert
    fun insert(history: History)

    @Delete
    fun delectBatch(histories: List<History>)



}