package io.github.jd1378.otphelper

import io.github.jd1378.otphelper.data.local.entity.IgnoredNotif
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotifType
import io.github.jd1378.otphelper.repository.isNotificationIgnored
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IgnoredNotifMatchTest {
  private val pkg = "com.google.android.gm"

  private fun entry(type: IgnoredNotifType, data: String = "") =
      IgnoredNotif(packageName = pkg, type = type, typeData = data)

  @Test
  fun nothingIgnoredWhenListIsEmpty() {
    assertFalse(isNotificationIgnored(emptyList(), "42", "inbox"))
  }

  @Test
  fun applicationEntryIgnoresEveryNotification() {
    assertTrue(isNotificationIgnored(listOf(entry(IgnoredNotifType.APPLICATION)), "42", null))
  }

  @Test
  fun idEntryIgnoresOnlyThatId() {
    val entries = listOf(entry(IgnoredNotifType.NOTIFICATION_ID, "42"))
    assertTrue(isNotificationIgnored(entries, "42", null))
    assertFalse(isNotificationIgnored(entries, "43", null))
  }

  @Test
  fun tagEntryIgnoresOnlyThatTag() {
    val entries = listOf(entry(IgnoredNotifType.NOTIFICATION_TAG, "inbox"))
    assertTrue(isNotificationIgnored(entries, "42", "inbox"))
    assertFalse(isNotificationIgnored(entries, "42", "other"))
    assertFalse(isNotificationIgnored(entries, "42", null))
  }

  // every entry must be checked, not only the first one returned by the database
  @Test
  fun laterMatchingEntryIsNotHiddenByEarlierNonMatchingOne() {
    val entries =
        listOf(entry(IgnoredNotifType.NOTIFICATION_ID, "43"), entry(IgnoredNotifType.APPLICATION))
    assertTrue(isNotificationIgnored(entries, "42", null))
  }

  @Test
  fun smsOriginEntryDoesNotIgnoreNotifications() {
    assertFalse(
        isNotificationIgnored(listOf(entry(IgnoredNotifType.SMS_ORIGIN, "Bank")), "42", "inbox"))
  }
}
