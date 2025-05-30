@file:Suppress("SpellCheckingInspection")

package io.github.jd1378.otphelper

import io.github.jd1378.otphelper.utils.CodeExtractor
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

/** See [testing documentation](http://d.android.com/tools/testing). */
class CodeDetectionUnitTest {

  @Test
  fun dummyTest() {
    val msg = "doesnt have anything"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals(null, CodeExtractor().getCode(msg))
  }

  @Test
  fun dummyTest2() {
    val msg = "doesn't have code here"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals(null, CodeExtractor().getCode(msg))
  }

  @Test
  fun pasargadCode() {
    val msg =
        """پاسارگاد
خرید
اسنپ فود
مبلغ:1,555,000
رمز:1122334455
00:00:00"""

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor().getCode(msg))
  }

  @Test
  fun randomMovePooyaCode() {
    val msg =
        """انتقال به کارت
000000*0000
5,555,555
رمز پویا 1122334455"""

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor().getCode(msg))
  }

  @Test
  fun pasargadCodeWithRamzArzKeyword() {
    val msg =
        """پاسارگاد
خرید
سایت رمز-ارز یه چیزی 12421
مبلغ:1,555,000
رمز:1122334455
00:00:00"""

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor().getCode(msg))
  }

  @Test
  fun samanCode() {
    val msg =
        """بانک سامان
خريد
اسنپ
مبلغ 450,000 ريال
رمز 1122334455
زمان اعتبار رمز 12:00:00"""

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor().getCode(msg))
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

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor().getCode(msg))
  }

  // -------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------
  // --------------------------------- IGNORE ONLY TESTS ---------------------------------
  // -------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------

  @Test
  fun digikalaIgnore1() {
    var shouldIgnore =
        CodeExtractor()
            .shouldIgnore(
                """دیجی‌کالا
سلام عزیز
از کالاهایی که خریده‌اید راضی هستید؟ لطفا میزان رضایتتان را از طریق لینک زیر به ما بگویید.
https://www.digikala.com/transaction/rate/?RatingCode=x123456
همچنین میتوانید درباره کالا دیدگاه ثبت کنید و پس از تایید دیدگاه، از دیجی کلاب امتیاز بگیرید!""")

    Assert.assertTrue(shouldIgnore)
  }

  @Test
  fun vscodeIgnore() {
    var shouldIgnore = CodeExtractor().shouldIgnore("""your vscode is: 12312312""".trimIndent())
    Assert.assertTrue(shouldIgnore)
  }

  @Test
  fun pasargadShouldIgnore1() {
    val should =
        CodeExtractor()
            .shouldIgnore(
                """پاسارگاد
رمز اول0000*0000000
در1999/01/01
12:00:00
اشتباه وارد شده است""")

    Assert.assertTrue(should)
  }

  @Test
  fun iranKetabCode() {
    val msg =
        """کد فعالسازی شما در سایت ایران کتاب 
Code: 123456
www.iranketab.ir
لغو11"""

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun tiktokCode() {
    val msg = "[#][TikTok] 123456 is your verification code"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun instagramCode() {
    val msg = "123 456 is your Instagram code. Don't share it."
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun paypal2FACode() {
    val msg = """PayPal : 123456 est votre code de sécurité. Ne partagez pas votre code."""

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun amazonCode() {
    val msg = "123456 ist dein Amazon-Einmalkennwort. Teile es nicht mit anderen Personen."
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun googleCode() {
    val msg = "G-123456 is your Google verification code."
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun spanishCode1() {
    val msg = "Su codigo de verificacion de AAAA es 123456"
    val expectedCode = "123456"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals(expectedCode, CodeExtractor().getCode(msg))
  }

  @Test
  fun spanishCode2() {
    val msg =
        "BBB. Clave de firma: 1234. Introduce esta clave de un solo uso (OTP) en el formulario web para firmar (SMS CERTIFICADO)"
    val expectedCode = "1234"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals(expectedCode, CodeExtractor().getCode(msg))
  }

  @Test
  fun spanishCode3() {
    val msg = "123 456 es tu código de Instagram. No lo compartas."
    val expectedCode = "123456"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals(expectedCode, CodeExtractor().getCode(msg))
  }

  @Test
  fun spanishCode4() {
    val msg = "PayPal: Tu código de seguridad es 123456. No lo compartas con nadie."
    val expectedCode = "123456"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals(expectedCode, CodeExtractor().getCode(msg))
  }

  @Test
  fun spanishCode5() {
    val msg = "123456 es tu contraseña temporal de Amazon. No la compartas con nadie."
    val expectedCode = "123456"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals(expectedCode, CodeExtractor().getCode(msg))
  }

  @Test
  fun spanishPINCode() {
    val msg = "Para acceder a su información del SCSALUD introduzca el siguiente PIN: 123456"
    val expectedCode = "123456"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals(expectedCode, CodeExtractor().getCode(msg))
  }

  @Test
  fun shouldNotExtractAnythingFromWordsContainingOTP() {
    val msg = "123456 is your foOTPath."
    val msg2 = "your foOTPath is 123456."
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals(false, CodeExtractor().shouldIgnore(msg2))
    assertEquals(null, CodeExtractor().getCode(msg))
    assertEquals(null, CodeExtractor().getCode(msg2))
  }

  @Test
  fun shouldBeSensitiveToOTP() {
    val msg = "123456 is your OTP."
    val msg2 = "your otp is 123456."
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals(false, CodeExtractor().shouldIgnore(msg2))
    assertEquals("123456", CodeExtractor().getCode(msg))
    assertEquals("123456", CodeExtractor().getCode(msg2))
  }

  @Test
  fun kingsoftCode() {
    val msg = "【金山办公】验证码123456，10分钟内有效。验证码提供给他人可能导致账号被盗，请勿转发或泄露。"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun alibabaCode() {
    val msg = "【阿里巴巴】验证码123456，您正在登录验证，切勿将验证码泄露于他人，验证码15分钟内有效。"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun twBankingOneCode() {
    val msg = "請提防詐騙！密碼勿提供他人或輸入不明網頁，您的信用卡網路消費幣別新台幣金額9876元，交易驗證碼『123456』請十分鐘內認證"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun twBankingTwoCode() {
    val msg = "【銀行轉帳】OTP密碼1234567網頁識別碼ABCD轉入帳號後四碼8888、TWD\$9876，密碼勿告知他人以防詐騙"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("1234567", CodeExtractor().getCode(msg))
  }

  @Test
  fun twShopeeCode() {
    val msg = "【蝦皮購物】輸入 123456 以登入您的帳號，15 分鐘有效。請不要將驗證碼分享給任何人，包括蝦皮員工。"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun gitubCode() {
    val msg =
        """123456 is your GitHub authentication code.

@github.com #123456"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun metroHealthCode() {
    val msg = "MetroHealth: Your verification code is: 123456. This code expires at 12:00 AM EDT."
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun hdfcBankCode() {
    val msg =
        "123456 is the One Time Password (OTP) to 2FA Login of your HDFC securities trading and investment account."
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun russionCode() {
    val msg = "ваш код: 123456"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun hebrewCode1() {
    val msg = "סיסמתך היא: 123456"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun hebrewCode2() {
    val msg = "קוד הכניסה שלך הוא 123456"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun hebrewCode3() {
    val msg = "הקוד שלך הוא 123456"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun hebrewCode4() {
    val msg = "הסיסמה החד פעמית: 123456"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun binanceTRCode() {
    val msg =
        "[Binance TR] Doğrulama Kodu: 123456. Lütfen bu doğrulama kodunu Binance TR çalışanı dahil kimseyle paylaşmayın! B040"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun shouldNotExtractCodeLessThanFourChars() {
    val msg = "your code is 123"
    assertEquals(false, CodeExtractor().shouldIgnore(msg)) // shouldnt ignore, but shouldnt extract
    assertEquals(null, CodeExtractor().getCode(msg))

    val msg2 = "your code is 1234"
    assertEquals(false, CodeExtractor().shouldIgnore(msg2))
    assertEquals("1234", CodeExtractor().getCode(msg2))

    val msg3 = "123 is your code"
    assertEquals(false, CodeExtractor().shouldIgnore(msg3))
    assertEquals(null, CodeExtractor().getCode(msg3))

    val msg4 = "1234 is your code"
    assertEquals(false, CodeExtractor().shouldIgnore(msg4))
    assertEquals("1234", CodeExtractor().getCode(msg4))
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
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("au8kuf", CodeExtractor().getCode(msg))
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
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("1u8kuf", CodeExtractor().getCode(msg))
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

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun vietnameseCode1() {
    val msg =
        "GD truc tuyen VietinBank,ma GD 1533 ma OTP 123456 so tien 50000 tai web MOMOCE.QK tuyet doi KHONG cung cap OTP cho nguoi khac"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun vietnameseCode2() {
    val msg =
        "Tai khoan quy khach se bi tru 6000000 VND qua kenh giao dich truc tuyen. Neu dung, quy khach thuc hien nhap OTP 123456 de hoan tat. Neu khong, LH 19009247"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun vietnameseCode3() {
    val msg =
        "QK nhap ma OTP 123456 tuong ung voi ma giao dich 7709 de cap lai mat khau dich vu VietinBank iPay.TH QK khong thuc hien giao dich nay, LH 1900558868"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun unicodeIgnoreTest() {
    val msg = "some unicode is 123456"
    assertEquals(true, CodeExtractor().shouldIgnore(msg))
  }

  @Test
  fun fancodeIgnoreTest() {
    val msg = "some fancode is 123456"
    assertEquals(true, CodeExtractor().shouldIgnore(msg))
  }

  @Test
  fun encodeIgnoreTest() {
    val msg = "some encode is 123456"
    assertEquals(true, CodeExtractor().shouldIgnore(msg))
  }

  @Test
  fun decodeIgnoreTest() {
    val msg = "some decode is 123456"
    assertEquals(true, CodeExtractor().shouldIgnore(msg))
  }

  @Test
  fun barcodeIgnoreTest() {
    val msg = "some barcode is 123456"
    assertEquals(true, CodeExtractor().shouldIgnore(msg))
  }

  @Test
  fun codexIgnoreTest() {
    val msg = "some codex is 123456"
    assertEquals(true, CodeExtractor().shouldIgnore(msg))
  }

  @Test
  fun longCodeCaptureTest() {
    val msg = longtext
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun georgianCode1() {
    val msg = "(s/n an p/n) 987789123: 123456 kods vada gasdis 5 wuTSi"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun georgianCode2() {
    val msg = "ertjeradi kodi: 123456"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun russianCode1() {
    val msg = "Ваш код: 123456"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun italianCode1() {
    val msg = "Il tuo codice di sicurezza è: 123456"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun samaneNobatCode() {
    val msg =
        """
      کد تایید شما در سامانه نوبت  : 123456

      لغو11
      لغو11
    """
            .trimIndent()

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun chineseAlipayCode() {
    val msg = "【支付宝】校验码1234，付款金额169.00，你正在使用支付宝，需要进行校验，请勿向任何人提供您收到的短信校验码"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("1234", CodeExtractor().getCode(msg))
  }

  @Test
  fun japaneseMercari() {
    val msg =
        """[ログイン] 認証番号：123456

ログインが実行されます
心当たりがない場合は詐欺に注意して下さい
メルカリ"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun japaneseMyJCB() {
    val msg = "MyJCBアプリワンタイムパスコード「123456」、10分間有効です。MyJCBアプリへご入力下さい。偽サイトへの入力にご注意ください。"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun japaneseJapanRailwaySmartEX() {
    val msg =
        """
【スマートEX】ワンタイムパスワード:123456
この番号を入力してください。
有効期限は5分間です。
"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun japaneseJCB3DSecure() {
    val msg =
        """パスワード：123456
ご利用金額：JPY 54,321
株式会社ジェーシービー"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun japaneseJapanPostBank() {
    val msg =
        """【ゆうちょ銀行】確認コードは12345です。
このコードを、メールやSMSで誘導された先の偽サイトに入力しないよう、ご注意ください。"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("12345", CodeExtractor().getCode(msg))
  }

  @Test
  fun sacombank() {
    val msg =
        """Quy khach se MAT TIEN va THONG TIN neu cung cap Ma xac thuc giao dich (OTP) cho bat ky ai. Ma OTP (het han sau 1 phut) cua Quy khach la 123-456"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun ecarteBlue() {
    val msg =
        """Le code à saisir pour votre achat de 200,00 EUR est 12345678. Ne donnez ce code à personne. Si vous n'avez pas réalisé cet achat, appelez le 09.69.32.00.04."""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("12345678", CodeExtractor().getCode(msg))
  }

  @Test
  fun blueSmart() {
    val msg =
        """Your One Time Password for user account with BluSmart is 123456. Valid for 10 Mins. For any assistance call (redacted)."""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun oneTimePasswordVariant() {
    val msg =
        """
      Danmarks Statistik SMS Token

      One-Time Password:
      AAAAAAAA

      Expiration Date: TUE Jan 05 12:00:00 CET 2024
    """
            .trimIndent()

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("AAAAAAAA", CodeExtractor().getCode(msg))
  }

  @Test
  fun shouldNotExtractHebrewNonCode() {
    val msg = "בנקודת 123456"

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals(null, CodeExtractor().getCode(msg))
  }

  @Test
  fun finnishMicrosoft() {
    val msg = """Microsoft todennuksessa käytetään vahvistuskoodia 123456"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun finnishOTPCode() {
    val msg = """Kirjautumisen kertakäyttökoodisi on 123456"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun mTanNotWorkingIssue65() {
    val msg = """mTan-Code: 123456"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun telegramCode() {
    val msg =
        """Login code: 12345. Do not give this code to anyone, even if they say they are from Telegram!

This code can be used to log in to your Telegram account. We never ask it for anything else.

If you didn't request this code by trying to log in on another device, simply ignore this message."""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("12345", CodeExtractor().getCode(msg))
  }

  @Test
  fun miHoYoCode() {
    val msg = """【米哈游】验证码：123456（10分钟内有效）。您正在关闭新设备验证功能，请勿将验证码告诉他人哦。"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun bilibiliCode() {
    val msg = """【哔哩哔哩】123456短信登录验证码，5分钟内有效，请勿泄露。"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun plCode1() {
    val msg = "123456 - to Twój kod weryfikacyjny"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun plCode2() {
    val msg = "Twoje hasło do autoryzacji to: 123456."
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun russianCode2() {
    val msg = "10.04.2024 10:18 Parol dlya podtverzhdeniya: 123456. Perevod 800.00 RUB."
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun charteredCode() {
    val msg =
        """QJL - 123456 is the One Time P/W (OTP) for online transaction on your card. ...............

Your Online Transaction to -, XXXXXXXXXXXX in Online Banking initiated. Your eTAC is 9876543210. ........."""

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun koreanCode() {
    val msg = "[한국모바일인증(주)]본인확인 인증번호[123456]입니다. \"타인노출금지\""

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun russianCode() {
    val msg = "Сеансовый пароль Интернет-банка e-bgpb.by: 123456. Никому не сообщайте!"
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun frenchCode() {
    val msg =
        """[LINE1]
[LINE2]
[LINE3]
[LINE4]
Code d'authentification : AAAA1A"""

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("AAAA1A", CodeExtractor().getCode(msg))
  }

  @Test
  fun germanGooglePayCode() {
    val msg =
        """222222 ist der Google Pay Aktivierungscode für deine Trade Republic Karte Endziffer-0000. Dieser Code verliert seine Gültigkeit nach 5 Minuten. Wir werden Sie niemals bitten, diesen Code zu teilen."""

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("222222", CodeExtractor().getCode(msg))
  }

  @Test
  fun bankFundTransferCode() {
    val msg =
        """<#>123456 is OTP for your fund transfer to JANE DOE A/c no ending 5678, valid for 5 mins. Never share OTP. Team Abc Bank 858BTD4Un5"""

    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun tencentCode() {
    val msg =
        """[Tencent Technology] You are performing [QQ 57******2 login verification], verification code 123456. Providing it to others may lead to QQ being stolen. If it is not your own operation, please change your password."""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun tencentChineseCode() {
    val msg = """【腾讯科技】你正在「修改QQ8******1的密保手机」，验证码123456。提供给他人会导致QQ被盗，若非本人操作，请修改密码。"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun tiktokChineseCode() {
    val msg = """【抖音】验证码123456，用于手机登录，5分钟内有效。验证码提供给他人可能导致账号被盗，请勿泄露，谨防被骗。"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun russianYandexCode() {
    val msg = """123-456 — код для входа в Яндекс"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun hospitalCode1() {
    val msg =
        """[Hospital name]: Your verification code is: 123456. This code expires at 2:59 PM EDT."""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("123456", CodeExtractor().getCode(msg))
  }

  @Test
  fun chinaMobileCode() {
    val msg =
        """【验证密码】您的短信验证码为1234，切勿将验证码泄露于他人，1分钟内有效！尊敬的客户，您好！您正在中国移动线上渠道上办理永辉超市15元满减券，产品资费为15元/次(订购生效时间以订购成功短信为准)，若非本人操作，请勿泄露，任何索取行为均可能涉嫌诈骗。【中国移动】"""
    assertEquals(false, CodeExtractor().shouldIgnore(msg))
    assertEquals("1234", CodeExtractor().getCode(msg))
  }
}
