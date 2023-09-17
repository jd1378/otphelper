package io.github.jd1378.otphelper.utils

class CodeIgnore {
  companion object {

    private val ignoredWords =
        listOf("تخفیف", "takhfif", "off", "اشتباه وارد شده", "RatingCode", "vscode")
    private val ignoredWordsRegex =
        """\b(${ignoredWords.joinToString("|")})\b""".toRegex(
            setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))

    fun shouldIgnore(str: String): Boolean {
      return str.contains(ignoredWordsRegex)
    }
  }
}
