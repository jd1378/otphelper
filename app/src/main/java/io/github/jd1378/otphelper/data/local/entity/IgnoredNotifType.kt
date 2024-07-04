package io.github.jd1378.otphelper.data.local.entity

import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.model.Translatable

enum class IgnoredNotifType(override val translation: Int? = null) : Translatable {
  APPLICATION(R.string.application),
  NOTIFICATION_TAG(R.string.notification_tag),
  NOTIFICATION_ID(R.string.notification_id),
}
