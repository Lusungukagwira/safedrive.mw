package com.example.safedrivemw.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "offline_documents")
data class OfflineDocumentEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val applicationId: String = "",
    val documentType: String = "",
    val localFilePath: String = "",
    val fileName: String = "",
    val uploadStatus: String = "PENDING_SYNC", // "PENDING_SYNC", "SYNCING", "SYNCED", "SYNC_FAILED"
    val downloadUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
