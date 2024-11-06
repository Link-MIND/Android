package org.sopt.search.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan

fun applyBoldStyle(text: String?, searchQuery: String?): SpannableString {
  val spannable = SpannableString(text ?: "")

  if (!text.isNullOrEmpty() && !searchQuery.isNullOrEmpty()) {
    val startIndex = text.indexOf(searchQuery)
    if (startIndex != -1) {
      val endIndex = startIndex + searchQuery.length
      spannable.setSpan(
        StyleSpan(Typeface.BOLD),
        startIndex,
        endIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
      )
    }
  }
  return spannable
}
