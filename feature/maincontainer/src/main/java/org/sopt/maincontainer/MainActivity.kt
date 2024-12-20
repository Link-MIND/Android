package org.sopt.maincontainer

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import designsystem.components.dialog.LinkMindDialog
import org.orbitmvi.orbit.viewmodel.observe
import org.sopt.common.util.delSpace
import org.sopt.maincontainer.databinding.ActivityMainBinding
import org.sopt.ui.context.hideKeyboard
import org.sopt.ui.keyboard.KeyboardUtils
import org.sopt.ui.nav.DeepLinkUtil
import org.sopt.ui.view.onThrottleClick

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  private lateinit var navController: NavController

  private val viewModel by viewModels<MainViewModel>()

  private val linkMindDialog by lazy {
    LinkMindDialog(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    initView()
    collectState()
  }

  private fun initView() {
    setFcv()
    changeBottomNavigationFragment()
    setBottomVisible()
    viewModel.showThenHide()
  }

  private fun collectState() {
    viewModel.observe(this, state = ::render, sideEffect = ::handleSideEffect)
  }

  private fun render(mainState: MainState) {
    binding.bnvMain.isVisible = mainState.isBottomNavigationBarVisible
    binding.testCoach.isVisible = mainState.visibleBubbleMark
  }

  private fun handleSideEffect(sideEffect: MainSideEffect) {
    when (sideEffect) {
      is MainSideEffect.NavigateSaveLink -> navigateToDestination("featureSaveLink://saveLinkFragment?clipboardLink=")
    }
  }

  private fun setFcv() {
    val navHostFragment =
      supportFragmentManager.findFragmentById(R.id.fcv_main) as NavHostFragment
    navController = navHostFragment.navController
    binding.bnvMain.itemIconTintList = null
    navController.graph.setStartDestination(org.sopt.home.R.id.nav_graph_home)
    binding.bnvMain.setupWithNavController(navController)
    binding.bnvMain.setOnItemReselectedListener { }
    changeBottomNavigationFragment()
    onClickFab()
  }

  private fun changeBottomNavigationFragment() {
    binding.bnvMain.setOnItemSelectedListener {
      if (binding.bnvMain.menu.findItem(it.itemId).isChecked) {
        false
      } else {
        navController.apply {
          popBackStack(org.sopt.clip.R.id.nav_graph_clip, false)
          popBackStack(org.sopt.timer.R.id.nav_graph_timer, false)
          popBackStack(org.sopt.mypage.R.id.nav_graph_mypage, false)
          popBackStack(org.sopt.search.R.id.nav_graph_search, false)
          if (it.itemId == R.id.navigation_home) {
            popBackStack(org.sopt.home.R.id.nav_graph_home, true)
          }
        }
        navigateToDestination(it.itemId)
      }
    }
  }

  private fun setBottomVisible() {
    navController.addOnDestinationChangedListener { _, destination, _ ->
      if (destination.id == R.id.navigation_home || destination.id == R.id.navigation_clip ||
        destination.id == R.id.navigation_timer || destination.id == R.id.navigation_search
      ) {
        viewModel.updateBnvVisible(true)
      } else {
        viewModel.updateBnvVisible(false)
      }
    }
  }

  private fun onClickFab() {
    binding.fabMain.onThrottleClick {
      viewModel.navigateSaveLink()
    }
  }

  private fun navigateToDestination(itemId: Int): Boolean {
    return navigationMap[itemId]?.let { destination ->
      navController.navigate(destination)
      true
    } ?: false
  }

  private fun navigateToDestination(url: String) {
    val (request, navOptions) = DeepLinkUtil.getNavRequestPopUpAndAnimption(
      popUpToId = org.sopt.savelink.R.id.nav_graph_save_link,
      inclusive = true,
      url.delSpace(),
      enterAnim = org.sopt.mainfeature.R.anim.from_bottom,
      exitAnim = android.R.anim.fade_out,
      popEnterAnim = android.R.anim.fade_in,
      popExitAnim = org.sopt.mainfeature.R.anim.to_bottom,
    )
    navController.navigate(request, navOptions)
  }

  private val navigationMap = mapOf(
    R.id.navigation_home to org.sopt.home.R.id.nav_graph_home,
    R.id.navigation_clip to org.sopt.clip.R.id.nav_graph_clip,
    R.id.navigation_search to org.sopt.search.R.id.nav_graph_search,
    R.id.navigation_timer to org.sopt.timer.R.id.nav_graph_timer,
  )
  /*private val navigationMap = mapOf(
    R.id.navigation_home to org.sopt.home.R.id.nav_graph_home,
    R.id.navigation_clip to org.sopt.clip.R.id.nav_graph_clip,
    R.id.navigation_my to org.sopt.mypage.R.id.nav_graph_mypage,
    R.id.navigation_timer to org.sopt.timer.R.id.nav_graph_timer,
  )*/

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (!hasFocus) return
    viewModel.getRecentLink()
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    if (!clipboard.hasPrimaryClip()) return

    if (clipboard.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN) == false) return

    val item = clipboard.primaryClip?.getItemAt(0)?.coerceToText(applicationContext)
    if (item.isNullOrEmpty()) return

    val pasteData = item.toString()
    Log.d("recentLink", "${viewModel.container.stateFlow.value.clipboard} $pasteData")
    if (pasteData == viewModel.container.stateFlow.value.clipboard) return
    viewModel.updateClipBoard(pasteData)

    hideKeyBoard()

    val action: () -> Unit = {
      clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
    }
    showRevokeCommonDialog(action)
  }

  private fun showRevokeCommonDialog(deleteClipBoard: () -> Unit) {
    linkMindDialog.setTitle(org.sopt.mainfeature.R.string.clipboard_dialog_title)
      .setSubtitle(org.sopt.mainfeature.R.string.clipboard_dialog_sub_title)
      .setNegativeButton(org.sopt.mainfeature.R.string.negative_close_cancel) {
        linkMindDialog.dismiss()
        deleteClipBoard()
      }
      .setPositiveButton(org.sopt.mainfeature.R.string.positive_ok_save) {
        navigateToDestination("featureSaveLink://saveLinkFragment?clipboardLink=${viewModel.container.stateFlow.value.clipboard}")
        deleteClipBoard()
        linkMindDialog.dismiss()
      }
      .show()
  }

  private fun hideKeyBoard() {
    KeyboardUtils.removeKeyboardVisibilityListener(binding.root)
    this.hideKeyboard(binding.root)
  }

  companion object {
    @JvmStatic
    fun newInstance(context: Context) = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
  }
}
