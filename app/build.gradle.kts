@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(libs.plugins.linkmind.android.application)
  alias(libs.plugins.linkmind.android.hilt)
  alias(libs.plugins.google.service)
}

android {
  namespace = "org.sopt.linkmind"

  defaultConfig {
    applicationId = "org.sopt.linkmind"
    versionCode = 13
    versionName = "1.2.0"
  }
  viewBinding.enable = true
  buildTypes {
    getByName("release") {
      signingConfig = signingConfigs.getByName("debug")
      isMinifyEnabled = false
      proguardFiles(
        "proguard-rules.pro",
      )
    }
  }
}

dependencies {
  implementation(projects.core.network)
  implementation(projects.core.datastore)
  implementation(projects.core.auth)
  implementation(projects.core.authimpl)
  implementation(projects.core.designsystem)

  implementation(projects.data.oauthdata)
  implementation(projects.data.timer)
  implementation(projects.data.category)
  implementation(projects.data.user)
  implementation(projects.data.link)
  implementation(projects.data.oauthdata)
  implementation(projects.data.home)

  implementation(projects.dataRemote.timer)
  implementation(projects.dataRemote.category)
  implementation(projects.dataRemote.user)
  implementation(projects.dataRemote.link)
  implementation(projects.dataRemote.home)

  implementation(projects.feature.maincontainer)
  implementation(projects.feature.login)
  implementation(projects.feature.share)

  implementation(libs.timber)
  implementation(libs.kakao.login)
  implementation(libs.bundles.firebase)
  implementation(platform(libs.firebase.bom))
  implementation(libs.androidx.splashscreen)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.espresso.core)
  implementation(libs.amplitude.analytics)
}
