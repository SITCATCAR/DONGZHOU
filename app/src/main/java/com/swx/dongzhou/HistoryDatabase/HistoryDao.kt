package com.swx.dongzhou.HistoryDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.swx.dongzhou.Util.QRCodeType


@Dao
interface HistoryDao {

    @Query("select * from history where isFavorite = 1 order by favoriteAt desc")
    fun selectIsFavorite():List<History>

    @Query("select * from history order by createdAt desc")
    fun selectAll(): List<History>

    @Query("select * from history where type = :type ")
    fun selectByType(type: QRCodeType):List<History>

    @Query("select * from history where id = :id limit 1")
    fun selectById(id: Long): History?

    @Query("select * from history where type in (:types) order by createdAt desc")
    fun selectByTypes(types: List<@JvmSuppressWildcards QRCodeType>): List<History>

    @Query("update history set isFavorite = :isFavorite, favoriteAt = :favoriteAt where id = :id")
    fun updateFavorite(id: Long, isFavorite: Boolean, favoriteAt: Long?): Int

    @Query("delete from history where id in (:ids)")
    fun deleteByIds(ids: List<@JvmSuppressWildcards Long>): Int

    @Delete
    fun delect(history: History): Int

    @Insert
    fun insert(history: History): Long

    @Delete
    fun delectBatch(histories: List<@JvmSuppressWildcards History>): Int



}
