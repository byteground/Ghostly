package com.ghostly.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ghostly.database.entities.RemoteKeys

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("Select * From remote_key Where post_id = :id")
    suspend fun getRemoteKeyByPostId(id: String): RemoteKeys?

    @Query("Delete From remote_key")
    suspend fun clearRemoteKeys()

    @Query("Select created_at From remote_key Order By created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}