package org.sopt.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import designsystem.components.bottomsheet.LinkMindBottomSheet
import designsystem.components.toast.linkMindSnackBar
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
    navigateToSearch()
  }

  private fun initView() {
    initAdapter()
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
    binding.pbLinkmindHome.setProgressBarMain(homeState.calculateProgress())
    homeClipAdapter.submitList(homeState.categoryList)
    homeWeekLinkAdapter.submitList(homeState.weekBestLink)
    homeWeekRecommendLinkAdapter.submitList(homeState.recommendLink)
  }

  private fun handleSideEffect(sideEffect: HomeSideEffect) {
    when (sideEffect) {
      is HomeSideEffect.NavigateSearch -> navigateToDestination("featureMyPage://fragmentSearch")
      is HomeSideEffect.NavigateSetting -> navigateToDestination("featureMyPage://fragmentSetting")
      is HomeSideEffect.NavigateClipLink -> navigateToDestination(
        "featureSaveLink://ClipLinkFragment/${viewModel.container.stateFlow.value.categoryId}/${viewModel.container.stateFlow.value.categoryName}",
      )

      is HomeSideEffect.ShowBottomSheet -> showHomeBottomSheet()
      is HomeSideEffect.NavigateWebView -> {
        val encodedURL = URLEncoder.encode(viewModel.container.stateFlow.value.url, StandardCharsets.UTF_8.toString())
        navigateToDestination(
          "featureSaveLink://webViewFragment/${0}/${false}/${false}/$encodedURL",
        )
      }

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

  private fun navigateToSearch() {
    binding.clHomeSearch.onThrottleClick {
      viewModel.navigateSearch()
    }
  }

  private fun setClipAdapter() {
    homeClipAdapter = HomeClipAdapter(
      onClickClip = {
        viewModel.navigateClipLink(it.categoryId, it.categoryTitle)
      },
      onClickEmptyClip = {
        viewModel.showBottomSheet()
      },
    )
    binding.rvHomeClip.adapter = homeClipAdapter
    val spacingClipInPixels = resources.getDimensionPixelSize(R.dimen.spacing_11)
    binding.rvHomeClip.addItemDecoration(ItemDecoration(2, spacingClipInPixels))
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

  private fun showHomeBottomSheet() {
    val linkMindBottomSheet = LinkMindBottomSheet(requireContext())
    linkMindBottomSheet.show()
    linkMindBottomSheet.apply {
      setBottomSheetHint(org.sopt.mainfeature.R.string.home_new_clip_info)
      setTitle(org.sopt.mainfeature.R.string.home_add_clip)
      setErroMsg(org.sopt.mainfeature.R.string.home_error_clip_info)
      bottomSheetConfirmBtnClick {
        if (showErrorMsg()) return@bottomSheetConfirmBtnClick
        viewModel.saveCategoryTitle(it)
        dismiss()
        requireContext().linkMindSnackBar(binding.vSnack, "클립 생성 완료!", false)
      }
    }
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
