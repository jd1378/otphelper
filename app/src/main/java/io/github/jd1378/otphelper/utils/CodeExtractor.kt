package io.github.jd1378.otphelper.utils

class CodeExtractor {
  companion object {
    private val sensitiveWords =
        listOf(
            "code",
            "One[-\\s]Time[-\\s]Password",
            "کد",
            "رمز",
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
            "\\bהקוד\\W",
            "\\bקוד\\W",
            "\\bKodu\\W", // turkish
            "\\bKodunuz\\W", // turkish
            "\\bKodi\\W",
            "\\bKods\\W",
            "\\b(?:m|sms)?TAN\\W",
            "\\bcodice\\W", // "code" in italian
            "コード", // "code" in japanese
            "パスワード", // "password" in japanese
            "認証番号", // "authentication number" in japanese
            "ワンタイム", // "one time" in japanese
            "\\bvahvistuskoodi", // "confirmation code" in finnish
            "\\bkod\\W", // PL
            "\\bautoryzacji\\W", // PL
            "Parol\\s+dlya\\s+podtverzhdeniya", // russian
        )

    private val ignoredWords =
        listOf(
            "مقدار",
            "مبلغ",
            "amount",
            "برای",
            "-ارز",
            // avoids detecting space separated code as bunch of words:
            "[a-zA-Z0-9] [a-zA-Z0-9] [a-zA-Z0-9] [a-zA-Z0-9] ?",
        )

    private val currencyIndicators =
        listOf(
            "USD",
            "EUR",
            "GBP",
            "[$€£]",
        )

    private val generalCodeMatcher =
        """(?:${sensitiveWords.joinToString("|")})(?:\s*(?!${
                ignoredWords.joinToString("|")
            })(?:[^\s:.'"\d\u0660-\u0669\u06F0-\u06F9\-]|[\d\u0660-\u0669\u06F0-\u06F9,\s]+(?:${currencyIndicators.joinToString("|")})|[\d\u0660-\u0669\u06F0-\u06F9][^\d\u0660-\u0669\u06F0-\u06F9]))*\s*:?\s*(["'「]?)${""
              // this comment is to separate parts
          }([\d\u0660-\u0669\u06F0-\u06F9a-zA-Z\-]{4,}|(?: [\d\u0660-\u0669\u06F0-\u06F9a-zA-Z]){4,}|)\1?(?:[^\d\u0660-\u0669\u06F0-\u06F9a-zA-Z]|${'$'})"""
            .toRegex(
                setOf(
                    RegexOption.IGNORE_CASE,
                    RegexOption.MULTILINE,
                ))

    private val specialCodeMatcher =
        """([\d\u0660-\u0669\u06F0-\u06F9 ]{4,}(?=\s)|[\d\u0660-\u0669\u06F0-\u06F9]{4,})[^:]*(?:${sensitiveWords.joinToString("|")})"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))

    fun getCode(str: String): String? {
      val results = generalCodeMatcher.findAll(str).filter { it.groups[2]?.value != null }
      if (results.count() > 0) {
        // generalCodeMatcher also detects if the text contains "code" keyword
        // so we only run google's regex only if general regex did not capture the "code" group
        val foundCode =
            results
                .find { it.groups[2]!!.value.isNotEmpty() }
                ?.groups
                ?.get(2)
                ?.value
                ?.replace(" ", "")
                ?.replace("-", "")
        if (foundCode !== null) {
          return toEnglishNumbers(foundCode)
        }
        return toEnglishNumbers(
            specialCodeMatcher.find(str)?.groups?.get(1)?.value?.replace(" ", ""))
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
  }
}
