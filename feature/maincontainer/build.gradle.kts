@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(libs.plugins.linkmind.plugin.feature)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "org.sopt.maincontainer"
  buildFeatures {
    viewBinding = true
  }
}

dependencies {
  implementation(projects.feature.home)
  implementation(projects.feature.clip)
  implementation(projects.feature.mypage)
  implementation(projects.feature.timer)
  implementation(projects.feature.search)
  implementation(projects.feature.savelink)
  implementation(projects.core.datastore)
}
