package org.sopt.datastore.datastore

import kotlinx.coroutines.flow.Flow
import tech.thdev.useful.encrypted.data.store.preferences.ksp.annotations.UsefulPreferences
import tech.thdev.useful.encrypted.data.store.preferences.ksp.annotations.value.ClearValues
import tech.thdev.useful.encrypted.data.store.preferences.ksp.annotations.value.GetValue
import tech.thdev.useful.encrypted.data.store.preferences.ksp.annotations.value.SetValue

@UsefulPreferences
interface SecurityDataStore {
  @GetValue(KEY_ACCESSTOKEN)
  fun flowAccessToken(): Flow<String>

  @SetValue(KEY_ACCESSTOKEN)
  suspend fun setAccessToken(string: String)

  @GetValue(KEY_REFRESHTOKEN)
  fun flowRefreshToken(): Flow<String>

  @SetValue(KEY_REFRESHTOKEN)
  suspend fun setRefreshToken(string: String)

  @GetValue(KEY_DEVICETOKEN)
  fun flowDeviceToken(): Flow<String>

  @SetValue(KEY_DEVICETOKEN)
  suspend fun setDeviceToken(string: String)

  @GetValue(KEY_AUTOLOGIN)
  fun flowAutoLogin(): Flow<Boolean>

  @SetValue(KEY_AUTOLOGIN)
  suspend fun setAutoLogin(boolean: Boolean)

  @GetValue(KEY_FCM_ALLOWED)
  fun flowFcmAllowed(): Flow<Boolean>

  @SetValue(KEY_FCM_ALLOWED)
  suspend fun setFcmAllowed(boolean: Boolean)

  @GetValue(KEY_POPUP_VISIBILITY)
  fun flowPopupVisibility(): Flow<Boolean>

  @SetValue(KEY_POPUP_VISIBILITY)
  suspend fun setPopupVisibility(boolean: Boolean)

  @GetValue(KEY_MARKET_UPDATE)
  fun flowMarketUpdate(): Flow<Boolean>

  @SetValue(KEY_MARKET_UPDATE)
  suspend fun setMarketUpdate(boolean: Boolean)

  @GetValue(TOOLTIP)
  fun flowTooltip(): Flow<String>

  @SetValue(TOOLTIP)
  suspend fun setTooltip(boolean: String)

  @ClearValues
  suspend fun clearAll()

  companion object {
    const val KEY_ACCESSTOKEN = "key-accesstoken"
    const val KEY_REFRESHTOKEN = "key-refreshtoken"
    const val KEY_DEVICETOKEN = "key-devicetoken"
    const val KEY_AUTOLOGIN = "key-autologin"
    const val KEY_FCM_ALLOWED = "key-fcm-allowed"
    const val KEY_POPUP_VISIBILITY = "key-popup-dialog"
    const val KEY_MARKET_UPDATE = "key-market-update"
    const val TOOLTIP = "tooltip"
  }
}
