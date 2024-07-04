package io.github.jd1378.otphelper.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Upsert

/** Base DAO. */
interface BaseDao<T> {
  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(entity: T): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(vararg entity: T)

  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(entities: Collection<T>)

  @Update(onConflict = OnConflictStrategy.REPLACE) suspend fun update(entity: T)

  @Upsert suspend fun upsert(entity: T)

  @Delete suspend fun delete(entity: T): Int
}
