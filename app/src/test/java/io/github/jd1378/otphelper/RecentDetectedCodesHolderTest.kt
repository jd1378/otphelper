package io.github.jd1378.otphelper

import io.github.jd1378.otphelper.di.DUPLICATE_DETECTION_WINDOW_MS
import io.github.jd1378.otphelper.di.RecentDetectedCodesHolder
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecentDetectedCodesHolderTest {

  private val sig = "com.google.android.gm 123456 your code is 123456"
  private val otherSig = "com.google.android.gm 654321 your code is 654321"

  @Test
  fun firstDetectionIsNotDuplicate() {
    val holder = RecentDetectedCodesHolder()
    assertFalse(holder.isDuplicate(sig, 0L))
  }

  @Test
  fun sameSignatureWithinWindowIsDuplicate() {
    val holder = RecentDetectedCodesHolder()
    holder.isDuplicate(sig, 0L)
    assertTrue(holder.isDuplicate(sig, 1_000L))
    assertTrue(holder.isDuplicate(sig, DUPLICATE_DETECTION_WINDOW_MS))
  }

  @Test
  fun sameSignatureAfterWindowIsNotDuplicate() {
    val holder = RecentDetectedCodesHolder()
    holder.isDuplicate(sig, 0L)
    assertFalse(holder.isDuplicate(sig, DUPLICATE_DETECTION_WINDOW_MS + 1))
  }

  @Test
  fun repostsKeepExtendingTheWindow() {
    val holder = RecentDetectedCodesHolder()
    holder.isDuplicate(sig, 0L)
    // each repost lands within a window of the previous one -> all duplicates
    assertTrue(holder.isDuplicate(sig, DUPLICATE_DETECTION_WINDOW_MS))
    assertTrue(holder.isDuplicate(sig, 2 * DUPLICATE_DETECTION_WINDOW_MS))
  }

  @Test
  fun differentSignaturesAreIndependent() {
    val holder = RecentDetectedCodesHolder()
    assertFalse(holder.isDuplicate(sig, 0L))
    assertFalse(holder.isDuplicate(otherSig, 1_000L))
    assertTrue(holder.isDuplicate(sig, 2_000L))
  }
}
