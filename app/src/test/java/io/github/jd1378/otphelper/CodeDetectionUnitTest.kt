@file:Suppress("SpellCheckingInspection")

package io.github.jd1378.otphelper

import io.github.jd1378.otphelper.utils.CodeExtractor
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor

/** See [testing documentation](http://d.android.com/tools/testing). */
@RunWith(Parameterized::class)
class CodeDetectionUnitTest(
  private val name: String,
  private val message: String,
  private val shouldIgnore: Boolean,
  private val expectedCode: String?,
  private val skipCodeCheck: Boolean,
) {
  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "{index}: {0}")
    fun data(): List<Array<Any?>> {
      val yaml = Yaml(SafeConstructor(LoaderOptions()))
      val inputStream =
          CodeDetectionUnitTest::class.java.getResourceAsStream("/code_detection_tests.yaml")!!
      val cases: List<Map<String, Any?>> = yaml.load(inputStream)
      return cases.map { entry ->
        arrayOf(
            entry["name"] as String,
            entry["message"] as String,
            entry["shouldIgnore"] as Boolean,
            entry["expectedCode"] as String?,
            (entry["skipCodeCheck"] as? Boolean) ?: false,
        )
      }
    }
  }

  @Test
  fun shouldIgnore() {
    assertEquals("[$name] shouldIgnore", shouldIgnore, CodeExtractor().shouldIgnore(message))
  }

  @Test
  fun getCode() {
    if (skipCodeCheck) return
    assertEquals("[$name] getCode", expectedCode, CodeExtractor().getCode(message))
  }
}
