package io.github.jd1378.otphelper

import io.github.jd1378.otphelper.utils.CodeIgnore
import org.junit.Assert
import org.junit.Test

class CodeIgnoreUnitTest {
  @Test
  fun digikalaIgnore1() {
    var shouldIgnore =
        CodeIgnore.shouldIgnore(
            """
دیجی‌کالا
سلام عزیز
از کالاهایی که خریده‌اید راضی هستید؟ لطفا میزان رضایتتان را از طریق لینک زیر به ما بگویید.
https://www.digikala.com/transaction/rate/?RatingCode=x123456
همچنین میتوانید درباره کالا دیدگاه ثبت کنید و پس از تایید دیدگاه، از دیجی کلاب امتیاز بگیرید!
    """
                .trimIndent())
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
