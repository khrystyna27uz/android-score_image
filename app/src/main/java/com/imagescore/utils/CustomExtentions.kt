package com.imagescore.utils

import java.text.SimpleDateFormat
import java.util.*

const val DATE_PATTERN = "dd-MM-yy"

fun Long.toDateString(): String {
    val date = Date(this)
    return (SimpleDateFormat(DATE_PATTERN, Locale.US).format(date))
}