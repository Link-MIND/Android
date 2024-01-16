package org.sopt.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import designsystem.components.bottomsheet.LinkMindBottomSheet
import designsystem.components.toast.linkMindSnackBar
import org.orbitmvi.orbit.viewmodel.observe
import org.sopt.home.adapter.HomeClipAdapter
import org.sopt.home.adapter.HomeWeekLinkAdapter
import org.sopt.home.adapter.HomeWeekRecommendLinkAdapter
import org.sopt.home.adapter.ItemDecoration
import org.sopt.home.databinding.FragmentHomeBinding
import org.sopt.ui.base.BindingFragment
import org.sopt.ui.nav.DeepLinkUtil
import org.sopt.ui.view.onThrottleClick

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
    binding.pbLinkmindHome.setProgressBarMain(homeState.calculateProgress())
    homeClipAdapter.submitList(homeState.categoryList)
    homeWeekLinkAdapter.submitList(homeState.weekBestLink)
    homeWeekRecommendLinkAdapter.submitList(homeState.recommendLink)
  }

  private fun handleSideEffect(sideEffect: HomeSideEffect) {
    when (sideEffect) {
      is HomeSideEffect.NavigateSearch -> navigateToDestination("featureMyPage://fragmentSetting")
      is HomeSideEffect.NavigateSetting -> navigateToDestination("featureMyPage://fragmentSearch")
      is HomeSideEffect.NavigateClipLink -> navigateToDestination(
        "featureSaveLink://ClipLinkFragment?categoryId=${viewModel.container.stateFlow.value.categoryId}",
      )
      is HomeSideEffect.showBottomSheet -> showHomeBottomSheet()
      is HomeSideEffect.NavigateWebview -> navigateToDestination("featureSaveLink://webViewFragment?site=${viewModel.container.stateFlow.value.url}")
    }
  }

  private fun initAdapter() {
    setClipAdapter()
    setWeekLinkAdapter()
    setWeekRecommendAdapter()
    viewModel.getMainPageUserClip()
    viewModel.getRecommendSite()
    viewModel.getWeekBestLink()
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
        viewModel.navigateClipLink(it.categoryId)
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
      destination,
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
      setTitle(org.sopt.mainfeature.R.string.home_correction_clip)
      setErroMsg(org.sopt.mainfeature.R.string.home_error_clip_info)
      bottomSheetConfirmBtnClick {
        if (showErrorMsg()) return@bottomSheetConfirmBtnClick
        dismiss()
        requireContext().linkMindSnackBar(binding.root, "성공", false)
      }
    }
  }
}
