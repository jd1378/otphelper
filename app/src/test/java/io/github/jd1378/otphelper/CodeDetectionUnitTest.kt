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

  @Test
  fun iranKetabCode() {
    val msg = """کد فعالسازی شما در سایت ایران کتاب 
Code: 123456
www.iranketab.ir
لغو11"""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun tiktokCode() {
    val msg = "[#][TikTok] 123456 is your verification code"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun instagramCode() {
    val msg = "123 456 is your Instagram code. Don't share it."
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun paypal2FACode() {
    val msg = """PayPal : 123456 est votre code de sécurité. Ne partagez pas votre code."""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun amazonCode() {
    val msg = "123456 ist dein Amazon-Einmalkennwort. Teile es nicht mit anderen Personen."
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun googleCode() {
    val msg = "G-123456 is your Google verification code."
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun spanishCode1() {
    val msg = "Su codigo de verificacion de AAAA es 123456"
    val expectedCode = "123456"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals(expectedCode, CodeExtractor.getCode(msg))
  }

  @Test
  fun spanishCode2() {
    val msg =
        "BBB. Clave de firma: 1234. Introduce esta clave de un solo uso (OTP) en el formulario web para firmar (SMS CERTIFICADO)"
    val expectedCode = "1234"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals(expectedCode, CodeExtractor.getCode(msg))
  }

  @Test
  fun spanishCode3() {
    val msg = "123 456 es tu código de Instagram. No lo compartas."
    val expectedCode = "123456"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals(expectedCode, CodeExtractor.getCode(msg))
  }

  @Test
  fun spanishCode4() {
    val msg = "PayPal: Tu código de seguridad es 123456. No lo compartas con nadie."
    val expectedCode = "123456"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals(expectedCode, CodeExtractor.getCode(msg))
  }

  @Test
  fun spanishCode5() {
    val msg = "123456 es tu contraseña temporal de Amazon. No la compartas con nadie."
    val expectedCode = "123456"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals(expectedCode, CodeExtractor.getCode(msg))
  }

  @Test
  fun shouldNotExtractAnythingFromWordsContainingOTP() {
    val msg = "123456 is your foOTPath."
    val msg2 = "your foOTPath is 123456."
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals(false, CodeIgnore.shouldIgnore(msg2))
    assertEquals(null, CodeExtractor.getCode(msg))
    assertEquals(null, CodeExtractor.getCode(msg2))
  }

  @Test
  fun shouldBeSensitiveToOTP() {
    val msg = "123456 is your OTP."
    val msg2 = "your otp is 123456."
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals(false, CodeIgnore.shouldIgnore(msg2))
    assertEquals("123456", CodeExtractor.getCode(msg))
    assertEquals("123456", CodeExtractor.getCode(msg2))
  }

  @Test
  fun kingsoftCode() {
    val msg = "【金山办公】验证码123456，10分钟内有效。验证码提供给他人可能导致账号被盗，请勿转发或泄露。"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun gitubCode() {
    val msg = """123456 is your GitHub authentication code.

@github.com #123456"""
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun metroHealthCode() {
    val msg = "MetroHealth: Your verification code is: 123456. This code expires at 12:00 AM EDT."
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun hdfcBankCode() {
    val msg =
        "123456 is the One Time Password (OTP) to 2FA Login of your HDFC securities trading and investment account."
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun russionCode() {
    val msg = "ваш код: 123456"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun hebrewCode1() {
    val msg = "סיסמתך היא: 123456"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun hebrewCode2() {
    val msg = "קוד הכניסה שלך הוא 123456"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun hebrewCode3() {
    val msg = "הקוד שלך הוא 123456"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun hebrewCode4() {
    val msg = "הסיסמה החד פעמית: 123456"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun binanceTRCode() {
    val msg =
        "[Binance TR] Doğrulama Kodu: 123456. Lütfen bu doğrulama kodunu Binance TR çalışanı dahil kimseyle paylaşmayın! B040"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun shouldNotExtractCodeLessThanFourChars() {
    val msg = "your code is 123"
    assertEquals(false, CodeIgnore.shouldIgnore(msg)) // shouldnt ignore, but shouldnt extract
    assertEquals(null, CodeExtractor.getCode(msg))

    val msg2 = "your code is 1234"
    assertEquals(false, CodeIgnore.shouldIgnore(msg2))
    assertEquals("1234", CodeExtractor.getCode(msg2))

    val msg3 = "123 is your code"
    assertEquals(false, CodeIgnore.shouldIgnore(msg3))
    assertEquals(null, CodeExtractor.getCode(msg3))

    val msg4 = "1234 is your code"
    assertEquals(false, CodeIgnore.shouldIgnore(msg4))
    assertEquals("1234", CodeExtractor.getCode(msg4))
  }

  @Test
  fun atrustHandySignaturCode() {
    val msg =
        """Handy-Signatur für
 eid.oesterreich.gv.at
Vergleichswert
 v 7 n M Z S S H 6 l
TAN
 a u 8 k u f
Bitte überprüfen Sie alle Werte!
(5 Min. gültig)"""
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("au8kuf", CodeExtractor.getCode(msg))
  }

  @Test
  fun atrustHandySignaturCode2() {
    val msg =
        """Handy-Signatur für
 eid.oesterreich.gv.at
Vergleichswert
 v 7 n M Z S S H 6 l
TAN
 1 u 8 k u f
Bitte überprüfen Sie alle Werte!
(5 Min. gültig)"""
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("1u8kuf", CodeExtractor.getCode(msg))
  }

  @Test
  fun rabinCash2FA() {
    val msg =
        """کدامنیتی درخواست انتقال ارز از کیف‌پول رابین‌کش

مقدار ارز:
1000000IRT

کیف‌پول مقصد:
0000000000000000

code: 123456
لغو11"""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun vietnameseCode1() {
    val msg =
        "GD truc tuyen VietinBank,ma GD 1533 ma OTP 123456 so tien 50000 tai web MOMOCE.QK tuyet doi KHONG cung cap OTP cho nguoi khac"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun vietnameseCode2() {
    val msg =
        "Tai khoan quy khach se bi tru 6000000 VND qua kenh giao dich truc tuyen. Neu dung, quy khach thuc hien nhap OTP 123456 de hoan tat. Neu khong, LH 19009247"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun vietnameseCode3() {
    val msg =
        "QK nhap ma OTP 123456 tuong ung voi ma giao dich 7709 de cap lai mat khau dich vu VietinBank iPay.TH QK khong thuc hien giao dich nay, LH 1900558868"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }
}
