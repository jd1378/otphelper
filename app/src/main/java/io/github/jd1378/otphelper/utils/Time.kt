package io.github.jd1378.otphelper.utils

import java.util.Calendar

fun getTimeToMidnightMillis(): Long {
  val currentTime = Calendar.getInstance()
  val midnightTime =
      Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        add(Calendar.DAY_OF_MONTH, 1)
      }

  return midnightTime.timeInMillis - currentTime.timeInMillis
}
