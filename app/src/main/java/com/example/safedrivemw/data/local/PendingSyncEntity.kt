package com.example.safedrivemw.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "pending_sync_records")
data class PendingSyncEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val recordType: String = "", // "USER_PROFILE", "APPLICATION", "RENEWAL", "PAYMENT", "STATUS_UPDATE"
    val payloadJson: String = "",
    val syncStatus: String = "PENDING_SYNC", // "PENDING_SYNC", "SYNCING", "SYNCED", "SYNC_FAILED"
    val createdAt: Long = System.currentTimeMillis(),
    val retryCount: Int = 0
)
