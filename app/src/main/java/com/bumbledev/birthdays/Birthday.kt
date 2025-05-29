package com.bumbledev.birthdays

import java.time.LocalDate
import java.time.MonthDay

data class Birthday(
    val name: String,
    val dateWithYear: LocalDate?,
    val dateWithoutYear: MonthDay,
    val daysUntil: Int,
    val nextAge: Int?
)
