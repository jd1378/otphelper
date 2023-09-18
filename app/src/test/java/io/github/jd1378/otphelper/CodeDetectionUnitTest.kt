@file:Suppress("SpellCheckingInspection")

package io.github.jd1378.otphelper

import io.github.jd1378.otphelper.utils.CodeExtractor
import io.github.jd1378.otphelper.utils.CodeIgnore
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

/** See [testing documentation](http://d.android.com/tools/testing). */
class CodeDetectionUnitTest {
  @Test
  fun pasargadCode() {
    val msg = """پاسارگاد
خرید
اسنپ فود
مبلغ:1,555,000
رمز:1122334455
00:00:00"""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor.getCode(msg))
  }

  @Test
  fun randomMovePooyaCode() {
    val msg = """انتقال به کارت
000000*0000
5,555,555
رمز پویا 1122334455"""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor.getCode(msg))
  }

  @Test
  fun pasargadCodeWithRamzArzKeyword() {
    val msg = """پاسارگاد
خرید
سایت رمز-ارز یه چیزی 12421
مبلغ:1,555,000
رمز:1122334455
00:00:00"""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor.getCode(msg))
  }

  @Test
  fun samanCode() {
    val msg = """بانک سامان
خريد
اسنپ
مبلغ 450,000 ريال
رمز 1122334455
زمان اعتبار رمز 12:00:00"""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor.getCode(msg))
  }

  @Test
  fun sinaCode() {
    val msg =
        """*بانک سينا*
خريد
ايرانسل
مبلغ 10,000 ريال
رمز 1122334455
زمان اعتبار  12:00:00
تاريخ 1400/01/01-10:00"""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor.getCode(msg))
  }

  // -------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------
  // --------------------------------- IGNORE ONLY TESTS ---------------------------------
  // -------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------

  @Test
  fun digikalaIgnore1() {
    var shouldIgnore =
        CodeIgnore.shouldIgnore(
            """دیجی‌کالا
سلام عزیز
از کالاهایی که خریده‌اید راضی هستید؟ لطفا میزان رضایتتان را از طریق لینک زیر به ما بگویید.
https://www.digikala.com/transaction/rate/?RatingCode=x123456
همچنین میتوانید درباره کالا دیدگاه ثبت کنید و پس از تایید دیدگاه، از دیجی کلاب امتیاز بگیرید!""")

    Assert.assertTrue(shouldIgnore)
  }

  @Test
  fun vscodeIgnore() {
    var shouldIgnore = CodeIgnore.shouldIgnore("""your vscode is: 12312312""".trimIndent())
    Assert.assertTrue(shouldIgnore)
  }

  @Test
  fun pasargadShouldIgnore1() {
    val should =
        CodeIgnore.shouldIgnore(
            """پاسارگاد
رمز اول0000*0000000
در1999/01/01
12:00:00
اشتباه وارد شده است""")

    Assert.assertTrue(should)
  }
}
