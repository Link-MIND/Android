package org.sopt.login.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.sopt.common.intentprovider.IntentProvider
import org.sopt.common.intentprovider.MAIN
import org.sopt.datastore.datastore.SecurityDataStore
import org.sopt.login.databinding.ActivityLoginBinding
import org.sopt.oauthdomain.interactor.OAuthInteractor
import org.sopt.ui.context.toast
import org.sopt.ui.view.UiState
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
  private lateinit var binding: ActivityLoginBinding
  private val viewModel: LoginViewModel by viewModels()

  @Inject
  lateinit var kakaoAuthInteractor: OAuthInteractor

  @Inject
  lateinit var dataStore: SecurityDataStore

  @MAIN
  @Inject
  lateinit var intentProvider: IntentProvider

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)
    initCheckAutoLogin()
    initKakaoLoginBtnClickListener()
    initAuthStateObserver()
    initAllPopupVisible()

    binding.vpOnboarding.adapter = LoginViewPagerAdapter(this)
    binding.indicatorOnboarding.attachTo(binding.vpOnboarding)
  }

  private fun initCheckAutoLogin() {
    lifecycleScope.launch {
      if (dataStore.flowAutoLogin().first()) {
        val intent = intentProvider.getIntent()
        startActivity(intent)
      }
    }
  }

  private fun initAuthStateObserver() {
    viewModel.authState.flowWithLifecycle(lifecycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          dataStore.setFcmAllowed(state.data.fcmIsAllowed)
          when (state.data.isRegistered) {
            true -> {
              dataStore.setAutoLogin(true)
              val intent = intentProvider.getIntent()
              startActivity(intent)
            }

            false -> {
              val intent = intentProvider.getIntent()
              startActivity(intent)
            }
          }
        }

        is UiState.Failure -> {
          this@LoginActivity.toast(state.msg)
        }

        is UiState.Loading -> {}
        else -> {}
      }
    }.launchIn(lifecycleScope)
  }

  private fun initKakaoLoginBtnClickListener() {
    binding.btnKakaoLogin.setOnClickListener {
      lifecycleScope.launch {
        kakaoAuthInteractor.loginByKakao()
          .onSuccess {
            viewModel.authenticate(it.accessToken, "KAKAO")
          }.onFailure {
            this@LoginActivity.toast(it.message.toString())
          }
      }
    }
  }

  private fun initAllPopupVisible() {
    lifecycleScope.launch {
      dataStore.setPopupVisibility(true)
      dataStore.setMarketUpdate(true)
    }
  }

  companion object {
    @JvmStatic
    fun newInstance(context: Context) = Intent(context, LoginActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
  }
}
