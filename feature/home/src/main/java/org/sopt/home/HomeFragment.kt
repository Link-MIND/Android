package org.sopt.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe
import org.sopt.common.util.delSpace
import org.sopt.home.adapter.HomeClipAdapter
import org.sopt.home.adapter.HomeWeekLinkAdapter
import org.sopt.home.adapter.HomeWeekRecommendLinkAdapter
import org.sopt.home.adapter.ItemDecoration
import org.sopt.home.databinding.FragmentHomeBinding
import org.sopt.home.model.UpdatePriority
import org.sopt.model.home.PopupInfo
import org.sopt.ui.base.BindingFragment
import org.sopt.ui.nav.DeepLinkUtil
import org.sopt.ui.view.onThrottleClick
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class HomeFragment : BindingFragment<FragmentHomeBinding>({ FragmentHomeBinding.inflate(it) }) {

  private lateinit var homeClipAdapter: HomeClipAdapter
  private lateinit var homeWeekLinkAdapter: HomeWeekLinkAdapter
  private lateinit var homeWeekRecommendLinkAdapter: HomeWeekRecommendLinkAdapter
  private val viewModel by viewModels<HomeViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initView()
    collectState()
    navigateToSetting()
    navigateToAllClip()
  }

  private fun initView() {
    initAdapter()
    viewModel.showThenHide()
  }

  private fun collectState() {
    viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleSideEffect)
  }

  private fun render(homeState: HomeState) {
    binding.tvHomeToastProgressLink.text = homeState.readToastNum.toString()
    binding.tvAllToastNum.text = "/" + homeState.allToastNum.toString()
    binding.tvHomeUserName.text = homeState.nickName
    binding.tvHomeUserClipName.text = homeState.nickName
    binding.tvHomeToastLinkCount.text = "${homeState.readToastNum}개의 링크"
    binding.testCoach.isVisible = homeState.visibleBubbleMark
    binding.pbLinkmindHome.setProgressBarMain(homeState.calculateProgress())
    homeClipAdapter.submitList(homeState.recentSavedLink)
    homeWeekLinkAdapter.submitList(homeState.weekBestLink)
    homeWeekRecommendLinkAdapter.submitList(homeState.recommendLink)
  }

  private fun handleSideEffect(sideEffect: HomeSideEffect) {
    when (sideEffect) {
      is HomeSideEffect.NavigateSetting -> navigateToDestination("featureMyPage://fragmentSetting")
      is HomeSideEffect.NavigateClipLink -> navigateToDestination(
        "featureSaveLink://ClipLinkFragment/${viewModel.container.stateFlow.value.categoryId}/${viewModel.container.stateFlow.value.categoryName}",
      )

      is HomeSideEffect.NavigateSaveLink -> navigateToDestinationWithoutAnim("featureSaveLink://saveLinkFragment?clipboardLink=")
      is HomeSideEffect.NavigateWebView -> {
        val encodedURL = URLEncoder.encode(viewModel.container.stateFlow.value.url, StandardCharsets.UTF_8.toString())
        navigateToDestination(
          "featureSaveLink://webViewFragment/${0}/${false}/${false}/$encodedURL",
        )
      }

      is HomeSideEffect.NavigateAllClip -> navigateToDestinationWithoutAnim("featureSaveLink://ClipLinkFragment/0/전체 클립")
      is HomeSideEffect.ShowPopupInfo -> showPopupInfo(viewModel.container.stateFlow.value.popupList)
      is HomeSideEffect.ShowUpdateDialog -> showUpdateDialog(viewModel.container.stateFlow.value.marketUpdate)
    }
  }

  private fun initAdapter() {
    setClipAdapter()
    setWeekLinkAdapter()
    setWeekRecommendAdapter()
    viewModel.apply {
      getMainPageUserClip()
      getRecommendSite()
      getRecentSavedClip()
      getWeekBestLink()
      getPopupListInfo()
      checkMarketUpdateState()
    }
  }

  private fun navigateToSetting() {
    binding.ivHomeSetting.onThrottleClick {
      viewModel.navigateSetting()
    }
  }

  private fun navigateToAllClip() {
    binding.ivRecentClip.onThrottleClick {
      viewModel.navigateAllClip()
    }
  }

  private fun setClipAdapter() {
    homeClipAdapter = HomeClipAdapter(
      onClickClip = {
        viewModel.navigateWebview(it.linkUrl)
      },
      onClickEmptyClip = {
        viewModel.navigateSaveLink()
      },
    )
    binding.rvHomeClip.adapter = homeClipAdapter
  }

  private fun setWeekLinkAdapter() {
    homeWeekLinkAdapter = HomeWeekLinkAdapter(
      onClickWeekLink = {
        viewModel.navigateWebview(it.toastLink)
      },
    )
    binding.rvWeekLink.adapter = homeWeekLinkAdapter
  }

  private fun setWeekRecommendAdapter() {
    homeWeekRecommendLinkAdapter = HomeWeekRecommendLinkAdapter(
      onClickRecommendLink = {
        viewModel.navigateWebview(it.siteUrl ?: "")
      },
    )
    binding.rvHomeWeekRecommend.adapter = homeWeekRecommendLinkAdapter
    val spacingWeekRecommendInPixels = resources.getDimensionPixelSize(R.dimen.spacing_12)
    binding.rvHomeWeekRecommend.addItemDecoration(ItemDecoration(3, spacingWeekRecommendInPixels))
  }

  private fun navigateToDestination(destination: String) {
    val (request, navOptions) = DeepLinkUtil.getNavRequestNotPopUpAndOption(
      destination.delSpace(),
      enterAnim = org.sopt.mainfeature.R.anim.from_bottom,
      exitAnim = android.R.anim.fade_out,
      popEnterAnim = android.R.anim.fade_in,
      popExitAnim = org.sopt.mainfeature.R.anim.to_bottom,
    )
    findNavController().navigate(request, navOptions)
  }

  private fun navigateToDestinationWithoutAnim(destination: String) {
    val (request, navOptions) = DeepLinkUtil.getNavRequestNotPopUpAndOption(
      destination.delSpace(),
    )
    findNavController().navigate(request, navOptions)
  }

  private fun showPopupInfo(popupList: List<PopupInfo>) {
    popupList.forEach {
      if (viewModel.checkPopupDate(it.popupActiveStartDate, it.popupActiveEndDate)
      ) {
        val surveyDialog = SurveyDialogFragment.newInstance(
          it.popupImage,
          { viewModel.navigateWebview(it.popupLinkUrl) },
          { viewModel.patchPopupInvisible(it.popupId.toLong(), 7) },
          { viewModel.setPopupVisible() },
        )
        surveyDialog.show(parentFragmentManager, this.tag)
      }
    }
  }

  private fun showUpdateDialog(marketUpdate: UpdatePriority) {
    if (marketUpdate != UpdatePriority.EMPTY) {
      val marketUpdateDialog = MarketUpdateDialogFragment.newInstance(
        marketUpdate,
        { viewModel.setMarketUpdateVisible() },
      )
      marketUpdateDialog.show(parentFragmentManager, this.tag)
    }
  }
}
