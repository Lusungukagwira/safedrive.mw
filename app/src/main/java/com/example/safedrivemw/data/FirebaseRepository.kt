package com.example.safedrivemw.data

import android.content.Context
import android.net.Uri
import com.example.safedrivemw.data.local.AppRoomDatabase
import com.example.safedrivemw.data.local.OfflineDocumentEntity
import com.example.safedrivemw.data.local.PendingSyncEntity
import com.example.safedrivemw.data.sync.DataSyncWorker
import com.example.safedrivemw.data.sync.NetworkConnectivityObserver
import com.example.safedrivemw.db.LicenceApplicationRecord
import com.example.safedrivemw.db.SafeDriveDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

data class UserProfile(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "CANDIDATE",
    val accountStatus: String = "ACTIVE",
    val createdAt: Long = System.currentTimeMillis()
)

data class PaymentTransaction(
    val paymentId: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val txRef: String = "",
    val paymentMethod: String = "",
    val amountPaid: String = "",
    val paymentDate: Long = System.currentTimeMillis(),
    val paymentStatus: String = "SUCCESSFUL",
    val relatedApplicationId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val confirmationDetails: String = "Verified by provider"
)

data class DocumentMetadata(
    val documentId: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val documentName: String = "",
    val documentType: String = "",
    val storagePath: String = "",
    val uploadDate: Long = System.currentTimeMillis(),
    val verificationStatus: String = "PENDING_VERIFICATION",
    val relatedApplicationId: String = ""
)

data class ApplicationRecord(
    val id: String = "",
    val userId: String = "",
    val applicantName: String = "",
    val applicantType: String = "NEW_APPLICANT",
    val category: String = "",
    val schoolName: String = "",
    val submissionDate: String = "",
    val status: String = "Submitted",
    val assignedOfficer: String = "Unassigned",
    val reviewComments: String = "",
    val lastUpdatedDate: String = ""
)

data class ActivityLogEntry(
    val activityId: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val action: String = "",
    val description: String = "",
    val relatedRecordId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val previousStatus: String = "",
    val newStatus: String = ""
)

class FirebaseRepository {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage get() = FirebaseStorage.getInstance()
    private val gson = Gson()

    // Helper to check current connectivity
    fun isConnected(context: Context): Boolean {
        return NetworkConnectivityObserver(context).isCurrentlyConnected()
    }

    // 1. Audit / Activity Logging
    fun logActivity(
        action: String,
        description: String,
        relatedRecordId: String = "",
        previousStatus: String = "",
        newStatus: String = ""
    ) {
        try {
            val userId = auth.currentUser?.uid ?: "anonymous"
            val logEntry = ActivityLogEntry(
                userId = userId,
                action = action,
                description = description,
                relatedRecordId = relatedRecordId,
                previousStatus = previousStatus,
                newStatus = newStatus
            )
            firestore.collection("activity_logs")
                .document(logEntry.activityId)
                .set(logEntry)
        } catch (_: Exception) {}
    }

    // 2. User Profile Management (Offline-First)
    fun saveUserProfile(context: Context, profile: UserProfile, onComplete: (Boolean) -> Unit = {}) {
        if (isConnected(context)) {
            try {
                firestore.collection("users")
                    .document(profile.uid)
                    .set(profile)
                    .addOnSuccessListener {
                        logActivity("USER_REGISTER", "Created account for ${profile.email}", profile.uid)
                        onComplete(true)
                    }
                    .addOnFailureListener {
                        saveProfileLocally(context, profile, onComplete)
                    }
            } catch (_: Exception) {
                saveProfileLocally(context, profile, onComplete)
            }
        } else {
            saveProfileLocally(context, profile, onComplete)
        }
    }

    private fun saveProfileLocally(context: Context, profile: UserProfile, onComplete: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppRoomDatabase.getInstance(context)
                val payloadJson = gson.toJson(profile)
                val pending = PendingSyncEntity(
                    userId = profile.uid,
                    recordType = "USER_PROFILE",
                    payloadJson = payloadJson,
                    syncStatus = "PENDING_SYNC"
                )
                db.pendingSyncDao().insertPendingSync(pending)
                DataSyncWorker.scheduleSync(context)
                onComplete(true)
            } catch (_: Exception) {
                onComplete(false)
            }
        }
    }

    fun getUserProfile(uid: String, onSuccess: (UserProfile?) -> Unit) {
        try {
            firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val profile = snapshot.toObject(UserProfile::class.java)
                        onSuccess(profile)
                    } else {
                        onSuccess(null)
                    }
                }
                .addOnFailureListener {
                    onSuccess(null)
                }
        } catch (_: Exception) {
            onSuccess(null)
        }
    }

    // 3. Document Upload (Offline-First)
    fun uploadDocument(
        context: Context,
        userId: String,
        applicationId: String,
        documentType: String,
        fileUri: Uri,
        fileName: String,
        onSuccess: (DocumentMetadata) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val documentId = UUID.randomUUID().toString()

        if (isConnected(context)) {
            val storageRef = storage.reference.child("documents/$userId/$applicationId/$fileName")
            try {
                storageRef.putFile(fileUri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val metadata = DocumentMetadata(
                                documentId = documentId,
                                userId = userId,
                                documentName = fileName,
                                documentType = documentType,
                                storagePath = uri.toString(),
                                relatedApplicationId = applicationId
                            )

                            firestore.collection("documents")
                                .document(documentId)
                                .set(metadata)
                                .addOnSuccessListener {
                                    logActivity("DOCUMENT_UPLOAD", "Uploaded $fileName ($documentType)", documentId)
                                    onSuccess(metadata)
                                }
                                .addOnFailureListener {
                                    saveDocumentLocally(context, userId, applicationId, documentType, fileUri, fileName, documentId, onSuccess)
                                }
                        }.addOnFailureListener {
                            saveDocumentLocally(context, userId, applicationId, documentType, fileUri, fileName, documentId, onSuccess)
                        }
                    }
                    .addOnFailureListener {
                        saveDocumentLocally(context, userId, applicationId, documentType, fileUri, fileName, documentId, onSuccess)
                    }
            } catch (_: Exception) {
                saveDocumentLocally(context, userId, applicationId, documentType, fileUri, fileName, documentId, onSuccess)
            }
        } else {
            saveDocumentLocally(context, userId, applicationId, documentType, fileUri, fileName, documentId, onSuccess)
        }
    }

    private fun saveDocumentLocally(
        context: Context,
        userId: String,
        applicationId: String,
        documentType: String,
        fileUri: Uri,
        fileName: String,
        documentId: String,
        onSuccess: (DocumentMetadata) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Copy file to app-private internal storage
                val docsDir = File(context.filesDir, "offline_docs")
                if (!docsDir.exists()) docsDir.mkdirs()

                val localFile = File(docsDir, "${documentId}_$fileName")
                context.contentResolver.openInputStream(fileUri)?.use { input ->
                    FileOutputStream(localFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val docEntity = OfflineDocumentEntity(
                    id = documentId,
                    userId = userId,
                    applicationId = applicationId,
                    documentType = documentType,
                    localFilePath = localFile.absolutePath,
                    fileName = fileName,
                    uploadStatus = "PENDING_SYNC"
                )

                val db = AppRoomDatabase.getInstance(context)
                db.offlineDocumentDao().insertDocument(docEntity)

                // Schedule background WorkManager sync
                DataSyncWorker.scheduleSync(context)

                val metadata = DocumentMetadata(
                    documentId = documentId,
                    userId = userId,
                    documentName = fileName,
                    documentType = documentType,
                    storagePath = localFile.absolutePath,
                    verificationStatus = "PENDING_SYNC",
                    relatedApplicationId = applicationId
                )
                onSuccess(metadata)
            } catch (_: Exception) {
                val metadata = DocumentMetadata(
                    documentId = documentId,
                    userId = userId,
                    documentName = fileName,
                    documentType = documentType,
                    storagePath = "local://$fileName",
                    verificationStatus = "PENDING_SYNC",
                    relatedApplicationId = applicationId
                )
                onSuccess(metadata)
            }
        }
    }

    // 4. Payment Management (Offline-First)
    fun recordPayment(
        context: Context,
        payment: PaymentTransaction,
        onComplete: (Boolean) -> Unit = {}
    ) {
        if (isConnected(context)) {
            try {
                firestore.collection("payments")
                    .document(payment.paymentId)
                    .set(payment)
                    .addOnSuccessListener {
                        logActivity(
                            action = "PAYMENT_COMPLETED",
                            description = "Paid ${payment.amountPaid} via ${payment.paymentMethod} (Ref: ${payment.txRef})",
                            relatedRecordId = payment.paymentId
                        )
                        onComplete(true)
                    }
                    .addOnFailureListener {
                        savePaymentLocally(context, payment, onComplete)
                    }
            } catch (_: Exception) {
                savePaymentLocally(context, payment, onComplete)
            }
        } else {
            savePaymentLocally(context, payment, onComplete)
        }
    }

    private fun savePaymentLocally(context: Context, payment: PaymentTransaction, onComplete: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppRoomDatabase.getInstance(context)
                val payloadJson = gson.toJson(payment)
                val pending = PendingSyncEntity(
                    userId = payment.userId,
                    recordType = "PAYMENT",
                    payloadJson = payloadJson,
                    syncStatus = "PENDING_SYNC"
                )
                db.pendingSyncDao().insertPendingSync(pending)
                DataSyncWorker.scheduleSync(context)
                onComplete(true)
            } catch (_: Exception) {
                onComplete(false)
            }
        }
    }

    fun getUserPayments(userId: String, onSuccess: (List<PaymentTransaction>) -> Unit) {
        try {
            firestore.collection("payments")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { query ->
                    val payments = query.toObjects(PaymentTransaction::class.java)
                    onSuccess(payments)
                }
                .addOnFailureListener {
                    onSuccess(emptyList())
                }
        } catch (_: Exception) {
            onSuccess(emptyList())
        }
    }

    // 5. Applications & Submissions (Offline-First)
    fun submitApplication(
        context: Context,
        appRecord: ApplicationRecord,
        onComplete: (Boolean) -> Unit = {}
    ) {
        // Also save to local SQLite SafeDriveDatabase so it's immediately visible across existing screens
        try {
            val localDb = SafeDriveDatabase.getInstance(context)
            val licenceRecord = LicenceApplicationRecord(
                id = appRecord.id,
                userId = appRecord.userId,
                applicantName = appRecord.applicantName,
                applicantType = appRecord.applicantType,
                category = appRecord.category,
                schoolName = appRecord.schoolName,
                status = appRecord.status,
                submittedDate = appRecord.submissionDate
            )
            localDb.addApplication(licenceRecord)
        } catch (_: Exception) {}

        if (isConnected(context)) {
            try {
                firestore.collection("applications")
                    .document(appRecord.id)
                    .set(appRecord)
                    .addOnSuccessListener {
                        logActivity(
                            action = "APPLICATION_SUBMIT",
                            description = "Submitted application ${appRecord.id} (${appRecord.category})",
                            relatedRecordId = appRecord.id,
                            newStatus = appRecord.status
                        )
                        onComplete(true)
                    }
                    .addOnFailureListener {
                        saveApplicationLocally(context, appRecord, onComplete)
                    }
            } catch (_: Exception) {
                saveApplicationLocally(context, appRecord, onComplete)
            }
        } else {
            saveApplicationLocally(context, appRecord, onComplete)
        }
    }

    private fun saveApplicationLocally(context: Context, appRecord: ApplicationRecord, onComplete: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppRoomDatabase.getInstance(context)
                val payloadJson = gson.toJson(appRecord)
                val pending = PendingSyncEntity(
                    userId = appRecord.userId,
                    recordType = "APPLICATION",
                    payloadJson = payloadJson,
                    syncStatus = "PENDING_SYNC"
                )
                db.pendingSyncDao().insertPendingSync(pending)
                DataSyncWorker.scheduleSync(context)
                onComplete(true)
            } catch (_: Exception) {
                onComplete(false)
            }
        }
    }

    fun updateApplicationStatus(
        context: Context,
        applicationId: String,
        previousStatus: String,
        newStatus: String,
        officerComments: String = "",
        onComplete: (Boolean) -> Unit = {}
    ) {
        try {
            val localDb = SafeDriveDatabase.getInstance(context)
            localDb.updateApplicationStatus(applicationId, newStatus)
        } catch (_: Exception) {}

        if (isConnected(context)) {
            try {
                val updates = mapOf(
                    "status" to newStatus,
                    "reviewComments" to officerComments,
                    "lastUpdatedDate" to System.currentTimeMillis().toString()
                )

                firestore.collection("applications")
                    .document(applicationId)
                    .update(updates)
                    .addOnSuccessListener {
                        logActivity(
                            action = "STATUS_CHANGE",
                            description = "Updated application status to $newStatus",
                            relatedRecordId = applicationId,
                            previousStatus = previousStatus,
                            newStatus = newStatus
                        )
                        onComplete(true)
                    }
                    .addOnFailureListener {
                        saveStatusUpdateLocally(context, applicationId, previousStatus, newStatus, officerComments, onComplete)
                    }
            } catch (_: Exception) {
                saveStatusUpdateLocally(context, applicationId, previousStatus, newStatus, officerComments, onComplete)
            }
        } else {
            saveStatusUpdateLocally(context, applicationId, previousStatus, newStatus, officerComments, onComplete)
        }
    }

    private fun saveStatusUpdateLocally(
        context: Context,
        applicationId: String,
        previousStatus: String,
        newStatus: String,
        officerComments: String,
        onComplete: (Boolean) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppRoomDatabase.getInstance(context)
                val officerUid = auth.currentUser?.uid ?: ""
                val payloadJson = gson.toJson(
                    mapOf(
                        "applicationId" to applicationId,
                        "previousStatus" to previousStatus,
                        "newStatus" to newStatus,
                        "officerComments" to officerComments
                    )
                )
                val pending = PendingSyncEntity(
                    userId = officerUid,
                    recordType = "STATUS_UPDATE",
                    payloadJson = payloadJson,
                    syncStatus = "PENDING_SYNC"
                )
                db.pendingSyncDao().insertPendingSync(pending)
                DataSyncWorker.scheduleSync(context)
                onComplete(true)
            } catch (_: Exception) {
                onComplete(false)
            }
        }
    }

    fun fetchUserApplications(context: Context, userId: String, onSuccess: (List<ApplicationRecord>) -> Unit) {
        if (isConnected(context)) {
            try {
                firestore.collection("applications")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener { query ->
                        val list = query.toObjects(ApplicationRecord::class.java)
                        if (list.isNotEmpty()) {
                            onSuccess(list)
                        } else {
                            onSuccess(fetchLocalApplications(context))
                        }
                    }
                    .addOnFailureListener {
                        onSuccess(fetchLocalApplications(context))
                    }
            } catch (_: Exception) {
                onSuccess(fetchLocalApplications(context))
            }
        } else {
            onSuccess(fetchLocalApplications(context))
        }
    }

    fun fetchLocalApplications(context: Context): List<ApplicationRecord> {
        return try {
            val localDb = SafeDriveDatabase.getInstance(context)
            localDb.getAllApplications().map { app ->
                ApplicationRecord(
                    id = app.id,
                    userId = app.userId,
                    applicantName = app.applicantName,
                    applicantType = app.applicantType,
                    category = app.category,
                    schoolName = app.schoolName,
                    submissionDate = app.submittedDate,
                    status = app.status,
                    reviewComments = app.correctionNotes
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun fetchActivityLogs(userId: String, onSuccess: (List<ActivityLogEntry>) -> Unit) {
        try {
            firestore.collection("activity_logs")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { query ->
                    val logs = query.toObjects(ActivityLogEntry::class.java)
                    onSuccess(logs)
                }
                .addOnFailureListener {
                    onSuccess(emptyList())
                }
        } catch (_: Exception) {
            onSuccess(emptyList())
        }
    }
}
