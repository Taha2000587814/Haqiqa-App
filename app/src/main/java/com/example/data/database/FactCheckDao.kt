package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FactCheckDao {
    @Query("SELECT * FROM fact_checks ORDER BY timestamp DESC")
    fun getAllFactChecks(): Flow<List<FactCheckResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFactCheck(result: FactCheckResult): Long

    @Query("DELETE FROM fact_checks WHERE id = :id")
    suspend fun deleteFactCheck(id: Long)

    @Query("DELETE FROM fact_checks")
    suspend fun clearAllFactChecks()

    // Usage limit / rate limit logging
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequestLog(log: RequestLog)

    @Query("SELECT * FROM request_logs WHERE timestamp >= :timestamp")
    suspend fun getRequestLogsAfter(timestamp: Long): List<RequestLog>

    @Query("DELETE FROM request_logs WHERE timestamp < :timestamp")
    suspend fun clearOldRequestLogs(timestamp: Long)
}
