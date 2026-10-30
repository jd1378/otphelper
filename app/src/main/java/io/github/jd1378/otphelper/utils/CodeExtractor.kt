package io.github.jd1378.otphelper.utils

import androidx.compose.runtime.Immutable
import io.github.jd1378.otphelper.utils.CodeExtractorDefaults.currencyIndicators
import io.github.jd1378.otphelper.utils.CodeExtractorDefaults.skipPhrases
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class CodeExtractorResult(
  val matchResult: MatchResult,
  val phraseGroup: Int,
  val codeGroup: Int,
)

// java's \b only treats ascii as word chars, so it never matches next to
// cyrillic/hebrew/persian letters. these are its unicode-aware equivalent.
private const val WORD_START = "(?<![\\p{L}\\p{N}_])"
private const val WORD_END = "(?![\\p{L}\\p{N}_])"

object CodeExtractorDefaults {
  val sensitivePhrases =
      persistentListOf(
          "code",
          "One[-\\s]Time[-\\s]Password",
          "کد",
          "رمز",
          "شناسه\\s+تا[یي][یي]د", // "confirmation id" in persian
          "\\bOTP\\W",
          "\\b2FA\\W",
          "Einmalkennwort",
          "contraseña",
          "c[oó]digo",
          "clave",
          "\\bel siguiente PIN\\W", // spanish
          "验证码",
          "校验码",
          "識別碼",
          "認證",
          "驗證",
          "код",
          "סיסמ",
          "${WORD_START}הקוד$WORD_END",
          "${WORD_START}קוד$WORD_END",
          "\\bKodu\\W", // "code" in turkish
          "\\bKodunuz\\W", // "your code" in turkish
          "\\b[sş]ifre:\\W", // "password" in turkish
          "\\b[sş]ifreniz(?=\\W)", // "your password" in turkish
          "\\bKodi\\W",
          "\\bKods\\W",
          "\\b(?:m|sms)?TAN\\W",
          "\\bcodice\\W", // "code" in italian
          "コード", // "code" in japanese
          "パスワード", // "password" in japanese
          "認証番号", // "authentication number" in japanese
          "ワンタイム", // "one time" in japanese
          "\\bvahvistuskoodi", // "confirmation code" in finnish
          "\\bkertakäyttökoodisi\\W", // "your single-use code" in finnish
          "\\bkod\\W", // PL
          "\\bautoryzacji\\W", // PL
          "Parol\\s+dlya\\s+podtverzhdeniya", // russian
          "${WORD_START}пароль$WORD_END", // russian
          "인증번호", // "authentication number" in korean
      )

  val skipPhrases =
      persistentListOf(
          "مقدار",
          "مبلغ",
          "amount",
          "برای",
          "-ارز",
          // avoids detecting space separated code as bunch of words:
          "[a-zA-Z0-9] [a-zA-Z0-9] [a-zA-Z0-9] [a-zA-Z0-9] ?",
      )

  val currencyIndicators =
      persistentListOf(
          "USD",
          "EUR",
          "GBP",
          "[$€£]",
      )

  val ignoredPhrases =
      persistentListOf(
          "تخفیف",
          "تخفیفات",
          "تخفیفها",
          "takhfif",
          "off",
          "اشتباه وارد شده",
          "RatingCode",
          "vscode",
          "versionCode",
          "unicode",
          "discount code",
          "fancode",
          "encode",
          "decode",
          "barcode",
          "codex",
      )

  val cleanupPhrases =
      persistentListOf(
          // before simpleDomainRegex, otherwise it only removes part of an address like
          // first.last1234@example.com and the leftover digits can be detected as the code
          "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)+", // simpleEmailRegex
          "[a-zA-Z0-9][a-zA-Z0-9-]{0,61}\\.[a-zA-Z]{2,}(?:[.a-zA-Z]{0,3}(?=\\s+)|)", // simpleDomainRegex
          "['\"]",
          "Endziffer-\\d+",
          "Ending \\d+",
          "<#>",
          "share OTP",
      )
}

class CodeExtractor // this comment is to separate parts
(
  private val sensitivePhrases: List<String> = CodeExtractorDefaults.sensitivePhrases,
  private val ignoredPhrases: List<String> = CodeExtractorDefaults.ignoredPhrases,
  private val cleanupPhrases: List<String> = CodeExtractorDefaults.cleanupPhrases,
) {

  val generalCodeMatcher: Regex =
      """(${sensitivePhrases.joinToString("|")})(?:\s*(?!${
        skipPhrases.joinToString("|")
      })(?:[^\s:：܃︓﹕.'"\d\u0660-\u0669\u06F0-\u06F9]|[\d\u0660-\u0669\u06F0-\u06F9,\s]+(?:${
        currencyIndicators.joinToString(
            "|",
        )
      })|[\d\u0660-\u0669\u06F0-\u06F9][^\d\u0660-\u0669\u06F0-\u06F9]))*\s*[:：܃︓﹕]?\s*(["'「]?)${
        ""
      }([\d\u0660-\u0669\u06F0-\u06F9a-zA-Z\-]{4,}|(?: [\d\u0660-\u0669\u06F0-\u06F9a-zA-Z]){4,}|)\1?(?:[^\d\u0660-\u0669\u06F0-\u06F9a-zA-Z]|${'$'})"""
          .toRegex(
              setOf(
                  RegexOption.IGNORE_CASE,
                  RegexOption.MULTILINE,
              ),
          )

  val specialCodeMatcher =
      """((?:[\d\u0660-\u0669\u06F0-\u06F9]-?){4,}(?=\s)|[\d\u0660-\u0669\u06F0-\u06F9 ]{4,}(?=\s)|[\d\u0660-\u0669\u06F0-\u06F9]{4,})[^:]*(${
        sensitivePhrases.joinToString(
            "|",
        )
      })"""
          .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))

  val ignoredPhrasesRegex =
      """$WORD_START(${ignoredPhrases.joinToString("|")})$WORD_END"""
          .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))

  val cleanupPhrasesRegex =
      """(${cleanupPhrases.joinToString("|")})"""
          .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))

  // doCleanup is added for convenience in unit testing
  fun getCode(str: String, doCleanup: Boolean = true): String? {
    if (sensitivePhrases.isEmpty()) return null
    val cleanStr =
        if (doCleanup) {
          cleanup(str)
        } else {
          str
        }
    val results = generalCodeMatcher.findAll(cleanStr).filter { it.groups[3]?.value != null }
    if (results.count() > 0) {
      // generalCodeMatcher also detects if the text contains "code" keyword
      // so we only run google's regex only if general regex did not capture the "code" group
      var foundCode =
          results
              .find { it.groups[3]!!.value.isNotEmpty() }
              ?.groups
              ?.get(3)
              ?.value
              ?.replace(" ", "")
              ?.replace("-", "")

      if (foundCode.isNullOrEmpty()) {
        foundCode =
            specialCodeMatcher
                .find(cleanStr)
                ?.groups
                ?.get(1)
                ?.value
                ?.replace(" ", "")
                ?.replace("-", "")
      }

      if (!foundCode.isNullOrEmpty()) {
        return toEnglishNumbers(foundCode)
      }
    }

    return null
  }

  fun getCodeMatch(str: String?): CodeExtractorResult? {
    if (str.isNullOrEmpty() || sensitivePhrases.isEmpty()) return null

    val results = generalCodeMatcher.findAll(str).filter { it.groups[3]?.value != null }
    if (results.count() > 0) {
      // generalCodeMatcher also detects if the text contains "code" keyword
      // so we only run google's regex only if general regex did not capture the "code" group
      var match = results.find { it.groups[3]!!.value.isNotEmpty() }
      val foundCode = match?.groups?.get(3)?.value?.replace(" ", "")?.replace("-", "")
      if (foundCode !== null) {
        return CodeExtractorResult(match!!, 1, 3)
      }
      match = specialCodeMatcher.find(str)
      if (match?.groups?.get(1)?.value?.replace(" ", "")?.replace("-", "")?.isNotBlank() == true) {
        return CodeExtractorResult(match, 2, 1)
      }
    }
    return null
  }

  private fun toEnglishNumbers(number: String?): String? {
    if (number.isNullOrEmpty()) return null
    val chars = CharArray(number.length)
    for (i in number.indices) {
      var ch = number[i]
      if (ch.code in 0x0660..0x0669) {
        ch -= (0x0660 - '0'.code)
      } else if (ch.code in 0x06f0..0x06F9) {
        ch -= (0x06f0 - '0'.code)
      }
      chars[i] = ch
    }
    return String(chars)
  }

  fun shouldIgnore(str: String): Boolean {
    if (ignoredPhrases.isEmpty()) return false
    return ignoredPhrasesRegex.containsMatchIn(str)
  }

  fun getIgnorePhrase(str: String): String? {
    if (ignoredPhrases.isEmpty()) return null
    return ignoredPhrasesRegex.find(str)?.value
  }

  fun cleanup(str: String): String {
    if (cleanupPhrases.isEmpty()) return str
    return str.replace(cleanupPhrasesRegex, "")
  }
}
