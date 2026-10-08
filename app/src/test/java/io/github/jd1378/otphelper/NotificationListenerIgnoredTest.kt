package io.github.jd1378.otphelper

import android.app.Application
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.UserHandle
import android.service.notification.StatusBarNotification
import androidx.room.Room
import io.github.jd1378.otphelper.data.local.db.OtpHelperDatabase
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotifType
import io.github.jd1378.otphelper.di.AutoUpdatingListenerUtils
import io.github.jd1378.otphelper.di.RecentDetectedCodesHolder
import io.github.jd1378.otphelper.di.RecentDetectedMessage
import io.github.jd1378.otphelper.di.RecentDetectedMessageHolder
import io.github.jd1378.otphelper.repository.IgnoredNotifsRepositoryImpl
import io.github.jd1378.otphelper.utils.CodeExtractor
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = Application::class)
class NotificationListenerIgnoredTest {
  private lateinit var database: OtpHelperDatabase
  private lateinit var listener: NotificationListener
  private lateinit var ignoredNotifs: IgnoredNotifsRepositoryImpl
  private lateinit var settings: AutoUpdatingListenerUtils
  private val context get() = RuntimeEnvironment.getApplication()
  private val packageName = "com.google.android.gm"
  private val text = "Your verification code is 123456"
  private val markReadAction = "otphelper.test.MARK_READ"

  @Before
  fun setUp() {
    database = Room.inMemoryDatabaseBuilder(context, OtpHelperDatabase::class.java).build()
    ignoredNotifs = IgnoredNotifsRepositoryImpl(database)
    settings = mockk(relaxed = true)
    every { settings.modeOfOperation } returns ModeOfOperation.Notification
    every { settings.isAutoMarkAsReadEnabled } returns true
    every { settings.isAutoDismissEnabled } returns true
    every { settings.codeExtractor } returns CodeExtractor(listOf("code"), emptyList(), emptyList())
    // Do not call onCreate: dependencies are supplied explicitly instead of through Hilt.
    listener = Robolectric.buildService(NotificationListener::class.java).get()
    listener.autoUpdatingListenerUtils = settings
    listener.ignoredNotifsRepository = ignoredNotifs
    listener.recentDetectedMessageHolder = RecentDetectedMessageHolder()
    listener.recentDetectedCodesHolder = RecentDetectedCodesHolder()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun ignoredAppKeepsNotificationUnreadAndVisible() = runBlocking {
    ignoredNotifs.setIgnored(packageName, IgnoredNotifType.APPLICATION)
    assertIgnored(notification())
  }

  @Test
  fun ignoredNotificationIdKeepsNotificationUnreadAndVisible() = runBlocking {
    ignoredNotifs.setIgnored(packageName, IgnoredNotifType.NOTIFICATION_ID, "42")
    assertIgnored(notification(id = 42))
  }

  @Test
  fun ignoredNotificationTagKeepsNotificationUnreadAndVisible() = runBlocking {
    ignoredNotifs.setIgnored(packageName, IgnoredNotifType.NOTIFICATION_TAG, "inbox")
    assertIgnored(notification(tag = "inbox"))
  }

  @Test
  fun nonMatchingIdStillMarksReadAndDismisses() = runBlocking {
    ignoredNotifs.setIgnored(packageName, IgnoredNotifType.NOTIFICATION_ID, "43")
    assertProcessed(notification(id = 42))
  }

  @Test
  fun nonMatchingTagStillMarksReadAndDismisses() = runBlocking {
    ignoredNotifs.setIgnored(packageName, IgnoredNotifType.NOTIFICATION_TAG, "other")
    assertProcessed(notification(tag = "inbox"))
  }

  @Test
  fun ignoredOtherAppDoesNotSuppressNotification() = runBlocking {
    ignoredNotifs.setIgnored("com.other.app", IgnoredNotifType.APPLICATION)
    assertProcessed(notification())
  }

  @Test
  fun ignoredAppInSmsModeDoesNotConsumeRecentSmsOrActOnNotification() = runBlocking {
    every { settings.modeOfOperation } returns ModeOfOperation.SMS
    ignoredNotifs.setIgnored(packageName, IgnoredNotifType.APPLICATION)
    listener.recentDetectedMessageHolder.message = RecentDetectedMessage(text, System.currentTimeMillis())
    assertIgnored(notification())
    assertNotNull(listener.recentDetectedMessageHolder.message)
  }

  @Test
  fun nonIgnoredNotificationInSmsModeStillMarksReadAndDismisses() = runBlocking {
    every { settings.modeOfOperation } returns ModeOfOperation.SMS
    listener.recentDetectedMessageHolder.message = RecentDetectedMessage(text, System.currentTimeMillis())
    assertProcessed(notification())
  }

  private fun notification(id: Int = 42, tag: String? = "inbox"): StatusBarNotification {
    val markRead = PendingIntent.getBroadcast(
        context, id, Intent(markReadAction).setPackage(context.packageName), PendingIntent.FLAG_IMMUTABLE,
    )
    val notification = Notification.Builder(context, "test")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentText(text)
        .addAction(Notification.Action.Builder(null, "Mark as read", markRead)
            .setSemanticAction(Notification.Action.SEMANTIC_ACTION_MARK_AS_READ).build())
        .build()
    return StatusBarNotification(packageName, packageName, id, tag, 1000, 0, 0,
        notification, UserHandle.getUserHandleForUid(1000), System.currentTimeMillis())
  }

  private suspend fun assertIgnored(sbn: StatusBarNotification) {
    // If the ignore check regresses, let detection finish so the read/dismiss assertions catch it.
    val detections = mockk<RecentDetectedCodesHolder>()
    every { detections.isDuplicate(any(), any()) } returns true
    listener.recentDetectedCodesHolder = detections
    shadowOf(listener).addActiveNotification(sbn)
    listener.processNotification(sbn)
    assertFalse(wasMarkedRead())
    assertEquals(1, listener.activeNotifications.size)
    verify(exactly = 0) { detections.isDuplicate(any(), any()) }
  }

  private suspend fun assertProcessed(sbn: StatusBarNotification) {
    // A repost exercises mark-as-read/dismiss without starting the clipboard/history worker.
    listener.recentDetectedCodesHolder.isDuplicate(signature(), System.currentTimeMillis())
    shadowOf(listener).addActiveNotification(sbn)
    listener.processNotification(sbn)
    assertTrue(wasMarkedRead())
    assertEquals(0, listener.activeNotifications.size)
  }

  private fun signature() = "$packageName|123456|$text\n"

  private fun wasMarkedRead() = shadowOf(context).broadcastIntents.any { it.action == markReadAction }
}
