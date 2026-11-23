package com.example.safedrivemw.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PendingSyncDao(private val dbHelper: AppRoomDatabase) {
    suspend fun insertPendingSync(record: PendingSyncEntity) = withContext(Dispatchers.IO) {
        try {
            val db = dbHelper.writableDatabase
            db.execSQL(
                "INSERT OR REPLACE INTO pending_sync_records (id, userId, recordType, payloadJson, syncStatus, createdAt, retryCount) VALUES (?, ?, ?, ?, ?, ?, ?)",
                arrayOf(record.id, record.userId, record.recordType, record.payloadJson, record.syncStatus, record.createdAt, record.retryCount)
            )
        } catch (_: Exception) {}
    }

    suspend fun updatePendingSync(record: PendingSyncEntity) = withContext(Dispatchers.IO) {
        try {
            val db = dbHelper.writableDatabase
            db.execSQL(
                "UPDATE pending_sync_records SET userId = ?, recordType = ?, payloadJson = ?, syncStatus = ?, createdAt = ?, retryCount = ? WHERE id = ?",
                arrayOf(record.userId, record.recordType, record.payloadJson, record.syncStatus, record.createdAt, record.retryCount, record.id)
            )
        } catch (_: Exception) {}
    }

    suspend fun getPendingSyncRecords(): List<PendingSyncEntity> = withContext(Dispatchers.IO) {
        val list = mutableListOf<PendingSyncEntity>()
        try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM pending_sync_records WHERE syncStatus = 'PENDING_SYNC' OR syncStatus = 'SYNC_FAILED'", null)
            if (cursor.moveToFirst()) {
                do {
                    list.add(
                        PendingSyncEntity(
                            id = cursor.getString(0) ?: "",
                            userId = cursor.getString(1) ?: "",
                            recordType = cursor.getString(2) ?: "",
                            payloadJson = cursor.getString(3) ?: "",
                            syncStatus = cursor.getString(4) ?: "PENDING_SYNC",
                            createdAt = cursor.getLong(5),
                            retryCount = cursor.getInt(6)
                        )
                    )
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (_: Exception) {}
        list
    }

    suspend fun getPendingCount(): Int = withContext(Dispatchers.IO) {
        var count = 0
        try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT COUNT(*) FROM pending_sync_records WHERE syncStatus = 'PENDING_SYNC' OR syncStatus = 'SYNC_FAILED'", null)
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
            cursor.close()
        } catch (_: Exception) {}
        count
    }

    suspend fun clearSyncedRecords() = withContext(Dispatchers.IO) {
        try {
            val db = dbHelper.writableDatabase
            db.execSQL("DELETE FROM pending_sync_records WHERE syncStatus = 'SYNCED'")
        } catch (_: Exception) {}
    }
}

class OfflineDocumentDao(private val dbHelper: AppRoomDatabase) {
    suspend fun insertDocument(doc: OfflineDocumentEntity) = withContext(Dispatchers.IO) {
        try {
            val db = dbHelper.writableDatabase
            db.execSQL(
                "INSERT OR REPLACE INTO offline_documents (id, userId, applicationId, documentType, localFilePath, fileName, uploadStatus, downloadUrl, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                arrayOf(doc.id, doc.userId, doc.applicationId, doc.documentType, doc.localFilePath, doc.fileName, doc.uploadStatus, doc.downloadUrl, doc.createdAt)
            )
        } catch (_: Exception) {}
    }

    suspend fun updateDocument(doc: OfflineDocumentEntity) = withContext(Dispatchers.IO) {
        try {
            val db = dbHelper.writableDatabase
            db.execSQL(
                "UPDATE offline_documents SET userId = ?, applicationId = ?, documentType = ?, localFilePath = ?, fileName = ?, uploadStatus = ?, downloadUrl = ?, createdAt = ? WHERE id = ?",
                arrayOf(doc.userId, doc.applicationId, doc.documentType, doc.localFilePath, doc.fileName, doc.uploadStatus, doc.downloadUrl, doc.createdAt, doc.id)
            )
        } catch (_: Exception) {}
    }

    suspend fun getPendingDocuments(): List<OfflineDocumentEntity> = withContext(Dispatchers.IO) {
        val list = mutableListOf<OfflineDocumentEntity>()
        try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM offline_documents WHERE uploadStatus = 'PENDING_SYNC' OR uploadStatus = 'SYNC_FAILED'", null)
            if (cursor.moveToFirst()) {
                do {
                    list.add(
                        OfflineDocumentEntity(
                            id = cursor.getString(0) ?: "",
                            userId = cursor.getString(1) ?: "",
                            applicationId = cursor.getString(2) ?: "",
                            documentType = cursor.getString(3) ?: "",
                            localFilePath = cursor.getString(4) ?: "",
                            fileName = cursor.getString(5) ?: "",
                            uploadStatus = cursor.getString(6) ?: "PENDING_SYNC",
                            downloadUrl = cursor.getString(7) ?: "",
                            createdAt = cursor.getLong(8)
                        )
                    )
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (_: Exception) {}
        list
    }

    suspend fun getPendingDocsCount(): Int = withContext(Dispatchers.IO) {
        var count = 0
        try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT COUNT(*) FROM offline_documents WHERE uploadStatus = 'PENDING_SYNC' OR uploadStatus = 'SYNC_FAILED'", null)
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
            cursor.close()
        } catch (_: Exception) {}
        count
    }
}

class AppRoomDatabase(context: Context) : SQLiteOpenHelper(context, "safedrive_offline.db", null, 1) {

    private val pendingDao = PendingSyncDao(this)
    private val docDao = OfflineDocumentDao(this)

    fun pendingSyncDao() = pendingDao
    fun offlineDocumentDao() = docDao

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE pending_sync_records (
                id TEXT PRIMARY KEY,
                userId TEXT,
                recordType TEXT,
                payloadJson TEXT,
                syncStatus TEXT,
                createdAt INTEGER,
                retryCount INTEGER
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE offline_documents (
                id TEXT PRIMARY KEY,
                userId TEXT,
                applicationId TEXT,
                documentType TEXT,
                localFilePath TEXT,
                fileName TEXT,
                uploadStatus TEXT,
                downloadUrl TEXT,
                createdAt INTEGER
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS pending_sync_records")
        db.execSQL("DROP TABLE IF EXISTS offline_documents")
        onCreate(db)
    }

    companion object {
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getInstance(context: Context): AppRoomDatabase {
            val appContext = runCatching { context.applicationContext }.getOrNull() ?: context
            return INSTANCE ?: synchronized(this) {
                val instance = AppRoomDatabase(appContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
