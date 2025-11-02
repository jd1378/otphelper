plugins {
  id("com.android.application") version "8.13.0" apply false
  id("com.android.library") version "8.13.0" apply false
  id("org.jetbrains.kotlin.android") version "2.2.21" apply false
  id("org.jetbrains.kotlin.plugin.compose") version "2.2.21" apply false
  id("com.google.dagger.hilt.android") version "2.57.2" apply false
  id("com.google.devtools.ksp") version "2.2.21-2.0.4" apply false
}

// https://chrisbanes.me/posts/composable-metrics/
// Usage:
// gradlew assembleRelease -PenableComposeCompilerReports=true
// metrics output: ./app/build/compose_metrics
// to force rerun tasks when data is stale:
// gradlew assembleRelease -PenableComposeCompilerReports=true --rerun-tasks
//subprojects {
//  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//    if (name.contains("ksp", ignoreCase = true)) {
//      return@configureEach
//    }
//
//    compilerOptions {
//      if (project.findProperty("enableComposeCompilerReports") == "true") {
//        freeCompilerArgs.addAll(
//            listOf(
//                "-P",
//                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
//                    project.layout.buildDirectory.get().asFile.absolutePath +
//                    "/compose_metrics",
//                "-P",
//                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
//                    project.layout.buildDirectory.get().asFile.absolutePath +
//                    "/compose_metrics",
//            ),
//        )
//      }
//    }
//  }
//}
