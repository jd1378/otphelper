package io.github.jd1378.otphelper.utils

class CodeExtractor {
  companion object {
    private val sensitiveWords =
        listOf(
            "code",
            "کد",
            "رمز",
            "\\bOTP\\b",
            "\\b2FA\\b",
            "Einmalkennwort",
            "contraseña",
            "c[oó]digo",
            "clave",
            "验证码",
            "код",
            "סיסמ",
            "קוד",
            "Kodunuz",
        )

    private val ignoredWords = listOf("مقدار", "مبلغ", "amount", "برای", "-ارز")
    private val generalCodeMatcher =
        """(?:${sensitiveWords.joinToString("|")})(?:\s*(?!${
                ignoredWords.joinToString("|")
            })[^\s:.'"\d\u0660-\u0669\u06F0-\u06F9])*[:.]?\s*(["']?)(?!${
                ignoredWords.joinToString(
                    "|"
                )
            })(?<code>[\d\u0660-\u0669\u06F0-\u06F9a-zA-Z]{4,}|)\1(?:[.\s][\n\t]|[.,，]|${'$'})"""
            .toRegex(
                setOf(
                    RegexOption.IGNORE_CASE,
                    RegexOption.MULTILINE,
                ))

    private val specialCodeMatcher =
        """(?<code>[\d ]+(?=\s)).*(?:${sensitiveWords.joinToString("|")})"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))

    fun getCode(str: String): String? {
      var results = generalCodeMatcher.findAll(str).filter { it.groups["code"] !== null }
      if (results.count() > 0) {
        // generalCodeMatcher also detects if the text contains "code" keyword
        // so we only run google's regex only if general regex did not capture the "code" group
        var foundCode =
            results
                .find { !it.groups["code"]!!.value.isNullOrEmpty() }
                ?.groups
                ?.get("code")
                ?.value
                ?.replace(" ", "")
        if (foundCode !== null) {
          return toEnglishNumbers(foundCode)
        }
        return toEnglishNumbers(
            specialCodeMatcher.find(str)?.groups?.get("code")?.value?.replace(" ", ""))
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
