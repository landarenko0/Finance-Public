package com.example.finance.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

fun LocalDate.toMillis(): Long =
    this.atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli()

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.systemDefault()).toLocalDate()

fun LocalDateTime.toMillis(): Long =
    this.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli()

fun Long.toLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.systemDefault()).toLocalDateTime()