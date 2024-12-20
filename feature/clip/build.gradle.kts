@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(libs.plugins.linkmind.plugin.feature)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "org.sopt.clip"
  buildFeatures {
    viewBinding = true
  }
}

dependencies {
  implementation(projects.domain.category)
  implementation(projects.domain.link)
  implementation(projects.core.datastore)
  implementation(libs.coil)
}
