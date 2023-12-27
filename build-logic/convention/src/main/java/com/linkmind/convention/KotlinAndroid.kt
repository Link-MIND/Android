package com.linkmind.convention

import org.gradle.api.Project
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

internal fun Project.configureKotlinAndroid(
  commonExtension: CommonExtension<*, *, *, *, *>,
) {
  commonExtension.apply {
    compileSdk = Const.compileSdk

    defaultConfig {
      minSdk = Const.minSdk

      testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
      vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
      sourceCompatibility = Const.JAVA_VERSION
      targetCompatibility = Const.JAVA_VERSION
    }

    kotlinOptions {
      jvmTarget = Const.JAVA_VERSION.toString()
    }

    dependencies.add("detektPlugins", libs.findLibrary("detekt-plugin-formatting").get())

    buildTypes {
      getByName("debug") {
        proguardFiles(
          getDefaultProguardFile("proguard-android.txt"),
          "proguard-debug.pro",
        )
      }

      getByName("release") {
        isMinifyEnabled = true
        proguardFiles(
          getDefaultProguardFile("proguard-android.txt"),
          "proguard-rules.pro",
        )
      }
    }
  }
}

internal fun CommonExtension<*, *, *, *, *>.kotlinOptions(
  block: KotlinJvmOptions.() -> Unit,
) {
  (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}
