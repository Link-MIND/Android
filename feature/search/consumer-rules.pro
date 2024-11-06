@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(libs.plugins.linkmind.plugin.feature)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "org.sopt.search"
  buildFeatures {
    viewBinding = true
  }
}

dependencies {
  implementation(libs.coil)
  implementation(libs.jsoup)
  implementation(libs.orbit.core)
  implementation(libs.orbit.viewmodel)
  implementation(libs.google.play.core)
  implementation(projects.core.datastore)
}
