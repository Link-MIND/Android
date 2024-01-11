@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(libs.plugins.linkmind.java.library)
}
dependencies {
  implementation(projects.core.model)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.hilt.core)
}
