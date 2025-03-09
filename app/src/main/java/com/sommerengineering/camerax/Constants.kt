package com.sommerengineering.camerax

import java.text.SimpleDateFormat
import java.util.Locale

val filename = SimpleDateFormat(
    "yyyy-MM-dd-HH-mm-ss-SSS",
    Locale.US)
        .format(System.currentTimeMillis())