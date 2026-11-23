package com.example.safedrivemw

import com.example.safedrivemw.db.LicenceApplicationRecord
import com.example.safedrivemw.db.NotificationRecord
import org.junit.Assert.*
import org.junit.Test

class SafeDriveIntegrationTests {

    // --- Database Entity Integration Tests ---

    @Test
    fun applicationRecord_statusTransitionWorkflow() {
        var application = LicenceApplicationRecord(
            id = "APP-2026-TEST",
            userId = "usr_100",
            applicantName = "Test Applicant",
            applicantType = "NEW_APPLICANT",
            category = "Class B (Light Motor)",
            schoolName = "Lilongwe Motor School",
            status = "Submitted",
            submittedDate = "2026-03-03"
        )

        assertEquals("Initial status should be Submitted", "Submitted", application.status)

        // Officer Verification
        application = application.copy(status = "Document Verification")
        assertEquals("Status should transition to Document Verification", "Document Verification", application.status)

        // Officer Review & Approval
        application = application.copy(status = "Approved")
        assertEquals("Status should transition to Approved", "Approved", application.status)
    }

    @Test
    fun notificationRecord_actionRequiredAlert() {
        val notification = NotificationRecord(
            id = "notif_test",
            userId = "usr_100",
            title = "Action Required: Medical Report",
            message = "Please re-upload a clearer copy of your Medical Fitness Report.",
            timestamp = "Just now",
            isRead = false,
            actionRequired = true
        )

        assertTrue("Notification should flag action required", notification.actionRequired)
        assertFalse("Notification should initially be unread", notification.isRead)
    }
}
