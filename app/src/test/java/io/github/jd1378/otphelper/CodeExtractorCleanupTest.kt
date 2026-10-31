package io.github.jd1378.otphelper

import io.github.jd1378.otphelper.utils.CodeExtractor
import org.junit.Assert.assertEquals
import org.junit.Test

class CodeExtractorCleanupTest {
  // quadratic backtracking takes several seconds on this input, linear matching takes milliseconds
  @Test(timeout = 1000)
  fun cleanupIsFastOnLongTextWithoutEmail() {
    val text = "a".repeat(50_000)
    assertEquals(text, CodeExtractor().cleanup(text))
  }
}
