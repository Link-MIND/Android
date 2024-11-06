package org.sopt.linkmind

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import org.sopt.linkmind.BuildConfig.KAKAO_NATIVE_KEY
import timber.log.Timber

@HiltAndroidApp
class LinkMindApp : Application() {
  override fun onCreate() {
    super.onCreate()
    setTimber()
    setDarkMode()
    setKakaoSdk()
    val amplitude = Amplitude(
      Configuration(
        apiKey = BuildConfig.AMPLITUDE_KEY,
        context = applicationContext,
      ),
    )
    amplitude.track("Sign Up")
  }

  private fun setTimber() {
    Timber.plant(Timber.DebugTree())
  }

  private fun setDarkMode() {
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
  }

  private fun setKakaoSdk() {
    KakaoSdk.init(this, KAKAO_NATIVE_KEY)
  }
}
