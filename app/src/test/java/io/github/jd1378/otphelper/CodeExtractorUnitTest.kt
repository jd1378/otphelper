@file:Suppress("SpellCheckingInspection")

package io.github.jd1378.otphelper

import io.github.jd1378.otphelper.utils.CodeExtractor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/** See [testing documentation](http://d.android.com/tools/testing). */
class CodeExtractorUnitTest {
  @Test
  fun pasargadCode() {
    val code =
        CodeExtractor.getCode(
            """پاسارگاد
خرید
اسنپ فود
مبلغ:1,555,000
رمز:123456
00:00:00"""
                .trimIndent())
    assertNotNull(code)
    assertEquals(code, "123456")
  }

  @Test
  fun randomMovePooyaCode() {
    val code = CodeExtractor.getCode("""انتقال به کارت
000000*0000
5,555,555
رمز پویا 1234567""")

    assertEquals(code, "1234567")
  }

  @Test
  fun pasargadCodeWithRamzArzKeyword() {
    val code =
        CodeExtractor.getCode(
            """پاسارگاد
خرید
سایت رمز-ارز یه چیزی 12421
مبلغ:1,555,000
رمز:123456
00:00:00"""
                .trimIndent())
    assertNotNull(code)
    assertEquals("123456", code)
  }
}
