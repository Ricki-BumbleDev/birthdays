package com.bumbledev.birthdays

import android.content.Context
import android.provider.ContactsContract
import java.time.LocalDate
import java.time.MonthDay
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ContactRepository(private val context: Context) {
    companion object {
        private val dateWithYearFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        private val dateWithoutYearFormatter = DateTimeFormatter.ofPattern("--MM-dd")
    }

    fun getBirthdays(): List<Birthday> = fetchBirthdays()
    
    private fun fetchBirthdays(): List<Birthday> {
        val uri = ContactsContract.Data.CONTENT_URI
        val projections = arrayOf(
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Event.START_DATE
        )
        val selection =
            "${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.Event.TYPE} = ?"
        val selectionArgs = arrayOf(
            ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY.toString()
        )
        val cursor = context.contentResolver.query(uri, projections, selection, selectionArgs, null)
            ?: return emptyList()
        val results = mutableListOf<Birthday>()
        val now = LocalDate.now()
        while (cursor.moveToNext()) {
            val name = cursor.getString(0)
            val dateString = cursor.getString(1)
            var dateWithYear: LocalDate? = null
            val dateWithoutYear: MonthDay
            try {
                if (dateString.startsWith("--")) {
                    dateWithoutYear = MonthDay.parse(dateString, dateWithoutYearFormatter)
                } else {
                    dateWithYear = LocalDate.parse(dateString, dateWithYearFormatter)
                    dateWithoutYear = MonthDay.from(dateWithYear)
                }
            } catch (e: Exception) {
                continue
            }

            var next = dateWithoutYear.atYear(now.year)
            if (next.isBefore(now))
                next = next.plusYears(1)
            val daysUntil = ChronoUnit.DAYS.between(now, next).toInt()
            val nextAge = dateWithYear?.let { birthDate -> next.year - birthDate.year }

            results.add(Birthday(name, dateWithYear, dateWithoutYear, daysUntil, nextAge))
        }
        cursor.close()
        return results.sortedBy { it.daysUntil }
    }
}