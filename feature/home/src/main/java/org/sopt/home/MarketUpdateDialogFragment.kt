package org.sopt.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import org.sopt.home.databinding.FragmentMarketUpdateDialogBinding
import org.sopt.home.model.UpdatePriority
import org.sopt.ui.base.BindingDialogFragment
import org.sopt.ui.view.onThrottleClick
import kotlin.system.exitProcess

class MarketUpdateDialogFragment : BindingDialogFragment<FragmentMarketUpdateDialogBinding>(
  { FragmentMarketUpdateDialogBinding.inflate(it) },
) {
  private var marketUpdatePriority: UpdatePriority? = null
  private var handleUpdate: () -> Unit = {}

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_FRAME, android.R.style.Theme_Dialog)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    checkUpdatePriority(marketUpdatePriority)
    binding.btnMarketUpdateDialogSkip.onThrottleClick {
      dismiss()
    }
    binding.btnMarketUpdateDialogUpdate.onThrottleClick {
      dismiss()
      navigateMarket()
      exitProcess(0)
    }
  }

  override fun onStart() {
    super.onStart()
    val dialog = dialog
    if (dialog != null) {
      dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      dialog.setCanceledOnTouchOutside(false)
    }
  }

  private fun checkUpdatePriority(marketUpdatePriority: UpdatePriority?) {
    when (marketUpdatePriority) {
      UpdatePriority.EMPTY -> {
        binding.tvMarketUpdateDialogTitle.text = "업데이트 알림"
        binding.tvMarketUpdateDialogSubtitle.text = "토스터의 사용성이 개선되었어요!\n지금 바로 업데이트해보세요"
        binding.btnMarketUpdateDialogUpdate.text = "지금 업데이트"
        binding.btnMarketUpdateDialogSkip.isInvisible = false
      }

      UpdatePriority.MINOR -> {
        binding.tvMarketUpdateDialogTitle.text = "업데이트 알림"
        binding.tvMarketUpdateDialogSubtitle.text = "토스터의 사용성이 개선되었어요!\n지금 바로 업데이트해보세요"
        binding.btnMarketUpdateDialogUpdate.text = "지금 업데이트"
        binding.btnMarketUpdateDialogSkip.isInvisible = false
      }

      UpdatePriority.MAJOR -> {
        binding.tvMarketUpdateDialogTitle.text = "기능 업데이트 알림"
        binding.tvMarketUpdateDialogSubtitle.text = "토스터의 기능이 추가되었어요!\n지금 바로 업데이트해보세요"
        binding.btnMarketUpdateDialogUpdate.text = "지금 업데이트"
        binding.btnMarketUpdateDialogSkip.isInvisible = false
      }

      UpdatePriority.CRITICAL -> {
        binding.tvMarketUpdateDialogTitle.text = "신규 기능 업데이트 알림"
        binding.tvMarketUpdateDialogSubtitle.text = "토스터의 새로운 기능을 이용하기 위해서는\n업데이트가 필요해요!\n최신 버전으로 업데이트 하시겠어요?"
        binding.btnMarketUpdateDialogUpdate.text = "업데이트 하기"
        binding.btnMarketUpdateDialogSkip.isInvisible = true
      }

      null -> {}
    }
  }

  private fun navigateMarket() {
    val appPackageName = "org.sopt.linkmind"
    runCatching {
      context?.packageManager?.getPackageInfo("com.android.vending", 0)
    }.onSuccess {
      startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
    }.onFailure {
      startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
    }
  }


  companion object {
    fun newInstance(
      updatePriority: UpdatePriority,
      updateButtonClick: () -> Unit,
    ): MarketUpdateDialogFragment {
      return MarketUpdateDialogFragment().apply {
        marketUpdatePriority = updatePriority
        handleUpdate = updateButtonClick
      }
    }
  }
}
