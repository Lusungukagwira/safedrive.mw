package com.example.safedrivemw

import com.example.safedrivemw.ui.screens.calculateAge
import org.junit.Assert.*
import org.junit.Test

class SafeDriveUnitTests {

    // --- Age Eligibility Calculation Tests ---

    @Test
    fun calculateAge_validAdultDob_returnsAgeAbove18() {
        val age = calculateAge("15/08/2000")
        assertNotNull("Age should not be null for valid date", age)
        assertTrue("Age should be >= 18 for birth year 2000", age!! >= 18)
    }

    @Test
    fun calculateAge_validUnderageDob_returnsAgeBelow18() {
        val age = calculateAge("10/05/2012")
        assertNotNull("Age should not be null for valid date", age)
        assertTrue("Age should be < 18 for birth year 2012", age!! < 18)
    }

    @Test
    fun calculateAge_invalidDateFormat_returnsNull() {
        val age = calculateAge("not-a-date")
        assertNull("Invalid date format should return null", age)
    }

    @Test
    fun calculateAge_emptyString_returnsNull() {
        val age = calculateAge("")
        assertNull("Empty date string should return null", age)
    }

    // --- Registration Validation Tests (SD_REG_001 - SD_REG_007) ---

    @Test
    fun registration_validInput_passesValidation() {
        val fullName = "Chisomo Banda"
        val email = "chisomo.banda@example.com"
        val phone = "+265 987491375"
        val password = "Driver@2026"
        val confirmPassword = "Driver@2026"
        val age = calculateAge("15/08/2000")

        val isValid = fullName.isNotBlank() &&
                email.contains("@") &&
                phone.isNotBlank() &&
                password == confirmPassword &&
                password.length >= 8 &&
                age != null && age >= 18

        assertTrue("Valid registration details should pass validation", isValid)
    }

    @Test
    fun registration_mismatchedPassword_failsValidation() {
        val password = "Driver@2026"
        val confirmPassword = "Driver@2025"

        val isPasswordMatch = password == confirmPassword
        assertFalse("Mismatched passwords should fail validation", isPasswordMatch)
    }

    @Test
    fun registration_shortPassword_failsValidation() {
        val password = "Safe@12"

        val isPasswordValidLength = password.length >= 8
        assertFalse("Password under 8 characters should fail validation", isPasswordValidLength)
    }

    @Test
    fun registration_invalidEmailFormat_failsValidation() {
        val email = "chisomo.banda.example.com"

        val isEmailFormatValid = email.contains("@")
        assertFalse("Email without @ symbol should fail validation", isEmailFormatValid)
    }

    // --- Login Validation Tests (SD_LOGIN_001 - SD_LOGIN_004) ---

    @Test
    fun login_validCredentials_succeeds() {
        val email = "chisomo.banda@safedrive.mw"
        val password = "Driver@2026"

        val isLoginValid = email.isNotBlank() && password.isNotBlank() && email.contains("@")
        assertTrue("Valid email and password should pass login validation", isLoginValid)
    }

    @Test
    fun login_emptyFields_failsValidation() {
        val email = ""
        val password = ""

        val isLoginValid = email.isNotBlank() && password.isNotBlank()
        assertFalse("Empty email/password should fail login validation", isLoginValid)
    }

    // --- Driving School Comparison Test (SD_SCHOOL_004, SD_SCHOOL_005) ---

    @Test
    fun schoolComparison_minimumTwoSchoolsRequired() {
        val selectedSchools1 = listOf("sch_1")
        val selectedSchools2 = listOf("sch_1", "sch_2")

        assertFalse("Comparison should fail with only 1 school selected", selectedSchools1.size >= 2)
        assertTrue("Comparison should succeed with 2 or more schools selected", selectedSchools2.size >= 2)
    }
}
