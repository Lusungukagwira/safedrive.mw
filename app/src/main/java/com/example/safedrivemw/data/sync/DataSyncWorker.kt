package com.example.safedrivemw.data.sync

import android.content.Context
import android.net.Uri
import androidx.work.*
import com.example.safedrivemw.data.*
import com.example.safedrivemw.data.local.AppRoomDatabase
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.concurrent.TimeUnit

class DataSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val db = AppRoomDatabase.getInstance(context)
    private val repo = FirebaseRepository()
    private val gson = Gson()

    override suspend fun doWork(): Result {
        var hasFailures = false
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid

        try {
            // 1. Synchronize Pending JSON Data Records (Profiles, Applications, Payments, Status Updates)
            val pendingRecords = db.pendingSyncDao().getPendingSyncRecords()
            for (record in pendingRecords) {
                try {
                    // Task 4: User ownership check. Skip if user logged out or record belongs to another account.
                    if (currentUid.isNullOrEmpty() || (record.userId.isNotBlank() && record.userId != currentUid && record.recordType != "STATUS_UPDATE")) {
                        continue
                    }

                    db.pendingSyncDao().updatePendingSync(record.copy(syncStatus = "SYNCING"))

                    // Task 1 & Task 2: Use Gson for robust JSON parsing and explicit recordType handlers
                    val success = when (record.recordType) {
                        "USER_PROFILE" -> {
                            val profile = gson.fromJson(record.payloadJson, UserProfile::class.java)
                            if (profile != null && profile.uid.isNotBlank()) {
                                FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(profile.uid)
                                    .set(profile, SetOptions.merge())
                                    .await()
                                true
                            } else false
                        }
                        "APPLICATION", "RENEWAL" -> {
                            val appRecord = gson.fromJson(record.payloadJson, ApplicationRecord::class.java)
                            if (appRecord != null && appRecord.id.isNotBlank()) {
                                FirebaseFirestore.getInstance()
                                    .collection("applications")
                                    .document(appRecord.id)
                                    .set(appRecord, SetOptions.merge())
                                    .await()
                                true
                            } else false
                        }
                        "PAYMENT" -> {
                            val payment = gson.fromJson(record.payloadJson, PaymentTransaction::class.java)
                            if (payment != null && payment.paymentId.isNotBlank()) {
                                FirebaseFirestore.getInstance()
                                    .collection("payments")
                                    .document(payment.paymentId)
                                    .set(payment, SetOptions.merge())
                                    .await()
                                true
                            } else false
                        }
                        "STATUS_UPDATE" -> {
                            val type = object : TypeToken<Map<String, String>>() {}.type
                            val map: Map<String, String>? = gson.fromJson(record.payloadJson, type)
                            val appId = map?.get("applicationId") ?: ""
                            val newStatus = map?.get("newStatus") ?: ""
                            val comments = map?.get("officerComments") ?: ""

                            if (appId.isNotBlank() && newStatus.isNotBlank()) {
                                val updates = mapOf(
                                    "status" to newStatus,
                                    "reviewComments" to comments,
                                    "lastUpdatedDate" to System.currentTimeMillis().toString()
                                )
                                FirebaseFirestore.getInstance()
                                    .collection("applications")
                                    .document(appId)
                                    .update(updates)
                                    .await()
                                true
                            } else false
                        }
                        // Task 2: Never mark unsupported record types as SYNCED!
                        else -> false
                    }

                    if (success) {
                        db.pendingSyncDao().updatePendingSync(record.copy(syncStatus = "SYNCED"))
                        repo.logActivity(
                            action = "WORKMANAGER_SYNC_SUCCESS",
                            description = "Background WorkManager synced record type ${record.recordType}",
                            relatedRecordId = record.id
                        )
                    } else {
                        hasFailures = true
                        db.pendingSyncDao().updatePendingSync(
                            record.copy(syncStatus = "SYNC_FAILED", retryCount = record.retryCount + 1)
                        )
                    }
                } catch (e: Exception) {
                    hasFailures = true
                    db.pendingSyncDao().updatePendingSync(
                        record.copy(syncStatus = "SYNC_FAILED", retryCount = record.retryCount + 1)
                    )
                }
            }

            // 2. Synchronize Pending Offline Document Uploads to Firebase Storage
            val pendingDocs = db.offlineDocumentDao().getPendingDocuments()
            for (doc in pendingDocs) {
                try {
                    // Task 4: User ownership check
                    if (currentUid.isNullOrEmpty() || (doc.userId.isNotBlank() && doc.userId != currentUid)) {
                        continue
                    }

                    db.offlineDocumentDao().updateDocument(doc.copy(uploadStatus = "SYNCING"))

                    val file = File(doc.localFilePath)
                    // Task 3: If local document file is missing, record failure and do NOT mark SYNCED!
                    if (!file.exists()) {
                        hasFailures = true
                        db.offlineDocumentDao().updateDocument(doc.copy(uploadStatus = "SYNC_FAILED"))
                        continue
                    }

                    val storageRef = FirebaseStorage.getInstance().reference.child(
                        "documents/${doc.userId}/${doc.applicationId}/${doc.fileName}"
                    )
                    val fileUri = Uri.fromFile(file)
                    storageRef.putFile(fileUri).await()
                    val downloadUrl = storageRef.downloadUrl.await().toString()

                    // Save metadata to Firestore documents collection
                    val docMetadata = DocumentMetadata(
                        documentId = doc.id,
                        userId = doc.userId,
                        documentName = doc.fileName,
                        documentType = doc.documentType,
                        storagePath = downloadUrl,
                        uploadDate = System.currentTimeMillis(),
                        verificationStatus = "PENDING_VERIFICATION",
                        relatedApplicationId = doc.applicationId
                    )
                    FirebaseFirestore.getInstance()
                        .collection("documents")
                        .document(doc.id)
                        .set(docMetadata, SetOptions.merge())
                        .await()

                    db.offlineDocumentDao().updateDocument(
                        doc.copy(uploadStatus = "SYNCED", downloadUrl = downloadUrl)
                    )

                    repo.logActivity(
                        action = "DOCUMENT_SYNC_SUCCESS",
                        description = "Background WorkManager uploaded document ${doc.fileName}",
                        relatedRecordId = doc.id
                    )
                } catch (e: Exception) {
                    hasFailures = true
                    db.offlineDocumentDao().updateDocument(doc.copy(uploadStatus = "SYNC_FAILED"))
                }
            }

            // Clean up successfully synced records
            db.pendingSyncDao().clearSyncedRecords()

            return if (hasFailures) Result.retry() else Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    companion object {
        const val UNIQUE_WORK_NAME = "SafeDriveDataSyncWork"

        fun scheduleSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWorkRequest = OneTimeWorkRequestBuilder<DataSyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                syncWorkRequest
            )
        }
    }
}
