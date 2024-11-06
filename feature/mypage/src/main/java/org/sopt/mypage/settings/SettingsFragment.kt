package org.sopt.mypage.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import designsystem.components.dialog.LinkMindDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.sopt.common.intentprovider.IntentProvider
import org.sopt.common.intentprovider.LOGIN
import org.sopt.datastore.datastore.SecurityDataStore
import org.sopt.model.user.MyPageData
import org.sopt.model.user.SettingPageData
import org.sopt.mypage.R
import org.sopt.mypage.databinding.FragmentSettingsBinding
import org.sopt.ui.fragment.viewLifeCycle
import org.sopt.ui.fragment.viewLifeCycleScope
import org.sopt.ui.view.UiState
import org.sopt.ui.view.onThrottleClick
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
  private lateinit var binding: FragmentSettingsBinding
  private val viewModel: SettingsViewModel by viewModels()

  @Inject
  @LOGIN
  lateinit var intentProvider: IntentProvider

  @Inject
  lateinit var dataStore: SecurityDataStore
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    binding = FragmentSettingsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.logoutState.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          dataStore.setAutoLogin(false)
          dataStore.setAccessToken("")
          dataStore.setRefreshToken("")
          val intent = intentProvider.getIntent()
          startActivity(intent)
          requireActivity().finish()
        }

        is UiState.Failure -> {
          dataStore.setAutoLogin(false)
          val intent = intentProvider.getIntent()
          startActivity(intent)
          requireActivity().finish()
        }

        else -> {
        }
      }
    }.launchIn(viewLifeCycleScope)

    viewModel.withdrawState.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          dataStore.setAutoLogin(false)
          val intent = intentProvider.getIntent()
          startActivity(intent)
          requireActivity().finish()
        }

        is UiState.Failure -> {
          dataStore.setAutoLogin(false)
          val intent = intentProvider.getIntent()
          startActivity(intent)
          requireActivity().finish()
        }

        else -> {
        }
      }
    }.launchIn(viewLifeCycleScope)

    viewModel.settingState.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          initSettingPageData(state.data)
          dataStore.setFcmAllowed(state.data.fcmIsAllowed)
        }

        else -> {}
      }
    }.launchIn(viewLifeCycleScope)

    viewModel.getUserInfo()

    viewModel.pushIsAllowed.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        true -> {
          dataStore.setFcmAllowed(true)
          binding.tvSettingsAlertOff.isGone = true
        }

        false -> {
          dataStore.setFcmAllowed(false)
          binding.tvSettingsAlertOff.isVisible = true
        }
      }
    }.launchIn(viewLifeCycleScope)

    viewModel.getUserMyPage()
    viewModel.myPageState.flowWithLifecycle(viewLifeCycle).onEach { state ->
      when (state) {
        is UiState.Success -> {
          initMyPageData(state.data)
        }
        else -> {}
      }
    }.launchIn(viewLifeCycleScope)

    onClickToggle()
    onClickLogoutBtn()
    onClickCloseBtn()
    onClickLeftBtn()
    onClickWithdrawBtn()
    onClick1On1Btn()
    onClickRuleBtn()
  }

  private fun onClickCloseBtn() {
    binding.ivSettingsClose.onThrottleClick {
      findNavController().navigateUp()
    }
  }

  private fun onClickLeftBtn() {
    binding.ivSettingsLeft.onThrottleClick {
      findNavController().navigateUp()
    }
  }

  private fun onClick1On1Btn() {
    binding.clSetting2.onThrottleClick {
      val url = URL_1ON1
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
      startActivity(intent)
    }
  }

  private fun onClickRuleBtn() {
    binding.clSetting3.onThrottleClick {
      val url = URL_RULE
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
      startActivity(intent)
    }
  }

  private fun onClickLogoutBtn() {
    binding.clSetting4.onThrottleClick {
      viewModel.logout()
    }
  }

  private fun onClickToggle() {
    binding.tgPushAllowed.onThrottleClick {
      viewModel.patchPush(!viewModel.pushIsAllowed.value)
      binding.settingsAlertToggle.transition()
    }
  }

  private fun onClickWithdrawBtn() {
    binding.clSetting5.setOnClickListener {
      val dialog = LinkMindDialog(requireContext())
      dialog.setTitle(org.sopt.mainfeature.R.string.setting_withdraw_dialog_title)
      dialog.setSubtitle(org.sopt.mainfeature.R.string.setting_withdraw_dialog_sub_title)
      dialog.setNegativeButton(org.sopt.mainfeature.R.string.negative_withdraw) {
        viewModel.withdraw()
        dialog.dismiss()
      }
      dialog.setPositiveButton(org.sopt.mainfeature.R.string.positive_withdraw) {
        dialog.dismiss()
      }
      dialog.show()
    }
  }

  private fun initSettingPageData(data: SettingPageData) {
    with(binding) {
      tvUserName.text = data.nickname
      if (data.fcmIsAllowed) {
        tvSettingsAlertOff.isGone = true
        settingsAlertToggle.initToggleState(true)
      } else {
        tvSettingsAlertOff.isVisible = true
        settingsAlertToggle.initToggleState(false)
      }
    }
  }

  private fun initMyPageData(data: MyPageData) {
    val myPage = data
    if (myPage != null) {
      with(binding) {
        tvUserName.text = myPage.nickname
        tvMyTotalLinkNum.text = myPage.allReadToast.toString()
        tvReadLinkThisWeekNum.text = myPage.thisWeekendRead.toString()
        tvSaveLinkThisWeekNum.text = myPage.thisWeekendSaved.toString()
        ivProfile.load(data.profile) { transformations(CircleCropTransformation()) }
      }
    }
  }

  companion object {
    const val URL_1ON1 = "https://open.kakao.com/o/sfN9Fr4f"
    const val URL_RULE = "https://www.notion.so/db429c114629431f8301a969ed028e37"
  }
}
