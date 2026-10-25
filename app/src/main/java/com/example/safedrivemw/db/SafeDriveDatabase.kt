package com.example.safedrivemw.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class UserRecord(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: String,
    val dob: String,
    val nationalId: String = ""
)

data class LicenceApplicationRecord(
    val id: String,
    val userId: String,
    val applicantName: String,
    val applicantType: String,
    val category: String,
    val schoolName: String,
    val status: String,
    val submittedDate: String,
    val correctionNotes: String = ""
)

data class DrivingSchoolRecord(
    val id: String,
    val name: String,
    val location: String,
    val priceMwk: String,
    val categoriesOffered: String,
    val phone: String,
    val rating: String
)

data class NotificationRecord(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val actionRequired: Boolean = false
)

class SafeDriveDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "safedrive_mw.db"
        private const val DATABASE_VERSION = 1

        @Volatile
        private var INSTANCE: SafeDriveDatabase? = null

        fun getInstance(context: Context): SafeDriveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = SafeDriveDatabase(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE users (
                id TEXT PRIMARY KEY,
                fullName TEXT NOT NULL,
                email TEXT NOT NULL,
                phone TEXT NOT NULL,
                role TEXT NOT NULL,
                dob TEXT NOT NULL,
                nationalId TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE applications (
                id TEXT PRIMARY KEY,
                userId TEXT NOT NULL,
                applicantName TEXT NOT NULL,
                applicantType TEXT NOT NULL,
                category TEXT NOT NULL,
                schoolName TEXT NOT NULL,
                status TEXT NOT NULL,
                submittedDate TEXT NOT NULL,
                correctionNotes TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE driving_schools (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                location TEXT NOT NULL,
                priceMwk TEXT NOT NULL,
                categoriesOffered TEXT NOT NULL,
                phone TEXT NOT NULL,
                rating TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE notifications (
                id TEXT PRIMARY KEY,
                userId TEXT NOT NULL,
                title TEXT NOT NULL,
                message TEXT NOT NULL,
                timestamp TEXT NOT NULL,
                isRead INTEGER NOT NULL,
                actionRequired INTEGER NOT NULL
            )
            """.trimIndent()
        )

        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS applications")
        db.execSQL("DROP TABLE IF EXISTS driving_schools")
        db.execSQL("DROP TABLE IF EXISTS notifications")
        onCreate(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        db.execSQL("INSERT INTO users VALUES ('usr_1', 'Chisomo Banda', 'chisomo.banda@safedrive.mw', '+265 999 123 456', 'CANDIDATE', '15/08/2002', 'NRB-987654321')")
        db.execSQL("INSERT INTO users VALUES ('usr_2', 'Officer Kondwani Phiri', 'k.phiri@rtd.gov.mw', '+265 888 234 567', 'RTD_OFFICER', '10/05/1985', 'RTD-OFF-402')")
        db.execSQL("INSERT INTO users VALUES ('usr_3', 'Lilongwe Motor School Admin', 'admin@lilongwemotor.mw', '+265 991 112 233', 'DRIVING_SCHOOL', '01/01/1990', 'SCH-LL-01')")

        db.execSQL("INSERT INTO applications VALUES ('APP-2026-001', 'usr_1', 'Chisomo Banda', 'NEW_APPLICANT', 'Class B (Light Motor)', 'Lilongwe Motor School', 'Under Review', '2026-03-01', '')")
        db.execSQL("INSERT INTO applications VALUES ('APP-2026-002', 'usr_1', 'Tiyanjane Mvula', 'RENEWAL', 'Class C1 (Medium Goods)', 'N/A (Direct Renewal)', 'Document Verification', '2026-03-02', 'Medical report stamp requires verification')")
        db.execSQL("INSERT INTO applications VALUES ('APP-2026-003', 'usr_1', 'Yamikani Phiri', 'NEW_APPLICANT', 'Class A (Motorcycles)', 'Blantyre Driving Academy', 'Approved', '2026-02-20', '')")

        db.execSQL("INSERT INTO driving_schools VALUES ('sch_1', 'Lilongwe Motor School', 'Lilongwe (Area 3 & City Centre)', 'MWK 150,000', 'Class A, Class B, Class C1', '+265 999 111 222', '4.8 ⭐')")
        db.execSQL("INSERT INTO driving_schools VALUES ('sch_2', 'Blantyre Driving Academy', 'Blantyre (Limbe & Ndirande)', 'MWK 140,000', 'Class B, Class C, Class D', '+265 888 333 444', '4.7 ⭐')")
        db.execSQL("INSERT INTO driving_schools VALUES ('sch_3', 'Mzuzu Road Safety School', 'Mzuzu (Central Hub)', 'MWK 135,000', 'Class A, Class B', '+265 991 555 666', '4.6 ⭐')")
        db.execSQL("INSERT INTO driving_schools VALUES ('sch_4', 'Zomba Universal Driving School', 'Zomba (University Area)', 'MWK 130,000', 'Class A, Class B, Class EB', '+265 882 777 888', '4.5 ⭐')")

        db.execSQL("INSERT INTO notifications VALUES ('notif_1', 'usr_1', 'Document Verification Update', 'Your National ID copy has been verified by RTD Officers.', 'Today, 09:30 AM', 0, 0)")
        db.execSQL("INSERT INTO notifications VALUES ('notif_2', 'usr_1', 'Action Required: Medical Certificate', 'Please re-upload a clearer copy of your Medical Fitness Report.', 'Yesterday, 02:15 PM', 0, 1)")
        db.execSQL("INSERT INTO notifications VALUES ('notif_3', 'usr_1', 'Driving School Request Accepted', 'Lilongwe Motor School has accepted your training request.', '2 days ago', 1, 0)")
    }

    fun getAllApplications(): List<LicenceApplicationRecord> {
        return try {
            val list = mutableListOf<LicenceApplicationRecord>()
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT * FROM applications", null)
            if (cursor.moveToFirst()) {
                do {
                    list.add(
                        LicenceApplicationRecord(
                            id = cursor.getString(0),
                            userId = cursor.getString(1),
                            applicantName = cursor.getString(2),
                            applicantType = cursor.getString(3),
                            category = cursor.getString(4),
                            schoolName = cursor.getString(5),
                            status = cursor.getString(6),
                            submittedDate = cursor.getString(7),
                            correctionNotes = cursor.getString(8) ?: ""
                        )
                    )
                } while (cursor.moveToNext())
            }
            cursor.close()
            list
        } catch (e: Exception) {
            getSampleApplications()
        }
    }

    fun getAllDrivingSchools(): List<DrivingSchoolRecord> {
        return try {
            val list = mutableListOf<DrivingSchoolRecord>()
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT * FROM driving_schools", null)
            if (cursor.moveToFirst()) {
                do {
                    list.add(
                        DrivingSchoolRecord(
                            id = cursor.getString(0),
                            name = cursor.getString(1),
                            location = cursor.getString(2),
                            priceMwk = cursor.getString(3),
                            categoriesOffered = cursor.getString(4),
                            phone = cursor.getString(5),
                            rating = cursor.getString(6)
                        )
                    )
                } while (cursor.moveToNext())
            }
            cursor.close()
            list
        } catch (e: Exception) {
            getSampleDrivingSchools()
        }
    }

    fun getAllNotifications(): List<NotificationRecord> {
        return try {
            val list = mutableListOf<NotificationRecord>()
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT * FROM notifications ORDER BY id DESC", null)
            if (cursor.moveToFirst()) {
                do {
                    list.add(
                        NotificationRecord(
                            id = cursor.getString(0),
                            userId = cursor.getString(1),
                            title = cursor.getString(2),
                            message = cursor.getString(3),
                            timestamp = cursor.getString(4),
                            isRead = cursor.getInt(5) == 1,
                            actionRequired = cursor.getInt(6) == 1
                        )
                    )
                } while (cursor.moveToNext())
            }
            cursor.close()
            list
        } catch (e: Exception) {
            getSampleNotifications()
        }
    }

    fun updateApplicationStatus(appId: String, newStatus: String, notes: String = "") {
        try {
            val db = writableDatabase
            val sql = "UPDATE applications SET status = ?, correctionNotes = ? WHERE id = ?"
            db.execSQL(sql, arrayOf(newStatus, notes, appId))
        } catch (ignored: Exception) {}
    }

    private fun getSampleApplications() = listOf(
        LicenceApplicationRecord("APP-2026-001", "usr_1", "Chisomo Banda", "NEW_APPLICANT", "Class B (Light Motor)", "Lilongwe Motor School", "Under Review", "2026-03-01", ""),
        LicenceApplicationRecord("APP-2026-002", "usr_1", "Tiyanjane Mvula", "RENEWAL", "Class C1 (Medium Goods)", "N/A (Direct Renewal)", "Document Verification", "2026-03-02", "Medical report stamp requires verification"),
        LicenceApplicationRecord("APP-2026-003", "usr_1", "Yamikani Phiri", "NEW_APPLICANT", "Class A (Motorcycles)", "Blantyre Driving Academy", "Approved", "2026-02-20", "")
    )

    private fun getSampleDrivingSchools() = listOf(
        DrivingSchoolRecord("sch_1", "Lilongwe Motor School", "Lilongwe (Area 3 & City Centre)", "MWK 150,000", "Class A, Class B, Class C1", "+265 999 111 222", "4.8 ⭐"),
        DrivingSchoolRecord("sch_2", "Blantyre Driving Academy", "Blantyre (Limbe & Ndirande)", "MWK 140,000", "Class B, Class C, Class D", "+265 888 333 444", "4.7 ⭐"),
        DrivingSchoolRecord("sch_3", "Mzuzu Road Safety School", "Mzuzu (Central Hub)", "MWK 135,000", "Class A, Class B", "+265 991 555 666", "4.6 ⭐"),
        DrivingSchoolRecord("sch_4", "Zomba Universal Driving School", "Zomba (University Area)", "MWK 130,000", "Class A, Class B, Class EB", "+265 882 777 888", "4.5 ⭐")
    )

    private fun getSampleNotifications() = listOf(
        NotificationRecord("notif_1", "usr_1", "Document Verification Update", "Your National ID copy has been verified by RTD Officers.", "Today, 09:30 AM", false, false),
        NotificationRecord("notif_2", "usr_1", "Action Required: Medical Certificate", "Please re-upload a clearer copy of your Medical Fitness Report.", "Yesterday, 02:15 PM", false, true),
        NotificationRecord("notif_3", "usr_1", "Driving School Request Accepted", "Lilongwe Motor School has accepted your training request.", "2 days ago", true, false)
    )
}
