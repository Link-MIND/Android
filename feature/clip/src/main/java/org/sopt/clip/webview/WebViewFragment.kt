package org.sopt.clip.webview

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import designsystem.components.toast.linkMindSnackBar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.sopt.clip.R
import org.sopt.clip.databinding.FragmentWebviewBinding
import org.sopt.ui.base.BindingFragment
import org.sopt.ui.context.hideKeyboard
import org.sopt.ui.fragment.viewLifeCycle
import org.sopt.ui.fragment.viewLifeCycleScope
import org.sopt.ui.view.onThrottleClick

@AndroidEntryPoint
class WebViewFragment : BindingFragment<FragmentWebviewBinding>({ FragmentWebviewBinding.inflate(it) }) {
  private val viewModel: WebViewViewModel by viewModels()
  val args: WebViewFragmentArgs by navArgs()
  var isPatched: Boolean = false
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.wbClip.settings.javaScriptEnabled = true
    val arg = args.site.split(",,,")
    if (arg.size == 3) {
      when (arg[2].toBoolean()) {
        true -> {
          viewModel.patchReadLinkResult.value = true
        }

        false -> {
          viewModel.patchReadLinkResult.value = false
        }
      }
      if (arg[1].toInt() == 0) {
        binding.ivRead.isInvisible = true
        binding.ivRead.isClickable = false
      }
    }
    Log.d("test", "$arg")

    binding.ivRead.onThrottleClick {
      Log.e("읽음", "누름")
      if (arg.size == 3) {
        viewModel.patchReadLink(arg[1].toLong(), !viewModel.patchReadLinkResult.value)
        isPatched = true
      }
    }

    viewModel.patchReadLinkResult.flowWithLifecycle(viewLifeCycle).onEach {
      when (it) {
        true -> {
          binding.ivRead.setImageResource(org.sopt.mainfeature.R.drawable.ic_read_after_24)
          if(isPatched) requireActivity().linkMindSnackBar(binding.clBottomBar, "열람 완료")
        }

        false -> {
          binding.ivRead.setImageResource(R.drawable.ic_read_before_24)
          if(isPatched) requireActivity().linkMindSnackBar(binding.clBottomBar, "열람 취소")
        }
      }
    }.launchIn(viewLifeCycleScope)
    Log.e("어케나오노", arg[0])
    setupWebView(arg[0])
    onClickClipLink()
    onClickWebViewClose()
    onClickWebViewReStart()
    // initReadBtnClickLister()
    initNavigationBtnClickListener()
    initBrowserBtnClickListener()
    initEditorActionListener()
  }

  private fun onClickClipLink() {
    val url = arguments?.getString("url")
    url?.let {
      setupWebView(it)
    }
  }

  private fun setupWebView(url: String?) {
    val webView = binding.wbClip
    val WebViewAddress = binding.tvWebviewAddress

    url?.let {
      webView.webViewClient = WebViewClient()
      webView.loadUrl(it)
      WebViewAddress.setText(it)
    }
  }

  private fun initEditorActionListener() {
    binding.tvWebviewAddress.setOnEditorActionListener { _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_DONE ||
        actionId == EditorInfo.IME_NULL ||
        actionId == EditorInfo.IME_ACTION_SEND ||
        actionId == EditorInfo.IME_ACTION_NEXT
      ) {
        val enteredUrl = binding.tvWebviewAddress.text.toString()
        if (enteredUrl.isNotBlank()) {
          binding.wbClip.loadUrl(enteredUrl)
          requireContext().hideKeyboard(requireView())
        }
        true
      } else {
        false
      }
    }
  }

  private fun initReadBtnClickLister() {
    with(binding) {
      ivRead.onThrottleClick {
        // handleVisibility(ivRead, ivReadAfter)
      }

      /*ivReadAfter.onThrottleClick {
        handleVisibility(ivReadAfter, ivRead)
      }*/
    }
  }

  private fun handleVisibility(visibleButton: View, invisibleButton: View) {
    visibleButton.isVisible = !visibleButton.isVisible
    invisibleButton.isVisible = !visibleButton.isVisible
  }

  private fun onClickWebViewClose() {
    binding.ivClose.onThrottleClick {
      findNavController().popBackStack()
    }
  }

  private fun onClickWebViewReStart() {
    val webView = binding.wbClip
    binding.ivWebviewRestart.onThrottleClick {
      webView.reload()
    }
  }

  private fun initNavigationBtnClickListener() {
    with(binding) {
      ivBack.onThrottleClick {
        if (wbClip.canGoBack()) {
          wbClip.goBack()
        }
        updateColors()
      }

      ivNext.onThrottleClick {
        if (wbClip.canGoForward()) {
          wbClip.goForward()
        }
        updateColors()
      }

      wbClip.webViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
          super.onPageStarted(view, url, favicon)

          runCatching { updateColors() }
        }
      }
    }
  }

  private fun updateColors() {
    with(binding) {
      ivBack.setColorFilter(
        ContextCompat.getColor(
          requireContext(),
          if (wbClip.canGoBack()) org.sopt.mainfeature.R.color.neutrals800 else org.sopt.mainfeature.R.color.neutrals150,
        ),
      )

      ivNext.setColorFilter(
        ContextCompat.getColor(
          requireContext(),
          if (wbClip.canGoForward()) org.sopt.mainfeature.R.color.neutrals800 else org.sopt.mainfeature.R.color.neutrals150,
        ),
      )
    }
  }

  private fun initBrowserBtnClickListener() {
    binding.ivInternet.onThrottleClick {
      val url = binding.wbClip.url
      if (url.isNullOrEmpty()) return@onThrottleClick
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
      startActivity(intent)
    }
  }
}
