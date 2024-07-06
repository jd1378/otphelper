package io.github.jd1378.otphelper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Stable
class DeepLinkHandler @Inject constructor() {
  val event = MutableStateFlow<Event>(Event.None)

  fun handleDeepLink(intent: Intent?) {
    if (intent != null) {
      // make a copy
      val editedIntent = Intent(intent)
      // this is to remove the FLAG_ACTIVITY_NEW_TASK flag, because navigation bugs otherwise
      editedIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP and Intent.FLAG_ACTIVITY_CLEAR_TOP

      event.update { Event.NavigateWithDeepLink(editedIntent) }
    }
  }

  fun consumeEvent() {
    event.update { Event.None }
  }
}

sealed interface Event {

  @Stable data class NavigateWithDeepLink(val intent: Intent) : Event

  data object None : Event
}

const val OTPHELPER_APP_SCHEME = "otphelper"

fun getDeepLinkPendingIntent(context: Context, route: String, navArgValue: String?): PendingIntent {
  var baseUri = "$OTPHELPER_APP_SCHEME://$route"
  if (!navArgValue.isNullOrEmpty()) {
    baseUri += "/$navArgValue"
  }
  val routeIntent =
      Intent(Intent.ACTION_VIEW, baseUri.toUri()).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
      }
  val flags =
      PendingIntent.FLAG_IMMUTABLE or
          PendingIntent.FLAG_UPDATE_CURRENT or
          PendingIntent.FLAG_CANCEL_CURRENT
  return PendingIntent.getActivity(context, 0, routeIntent, flags)
}
