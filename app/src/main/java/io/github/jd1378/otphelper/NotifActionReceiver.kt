package io.github.jd1378.otphelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.repository.IgnoredNotifsRepository
import io.github.jd1378.otphelper.worker.NotifActionWorker
import javax.inject.Inject

@AndroidEntryPoint
class NotifActionReceiver : BroadcastReceiver() {

  @Inject lateinit var ignoredNotifsRepository: IgnoredNotifsRepository

  companion object {
    const val INTENT_ACTION_CODE_COPY = "io.github.jd1378.otphelper.actions.code_copy"
    const val INTENT_ACTION_IGNORE_TAG_NOTIFICATION_TAG =
        "io.github.jd1378.otphelper.actions.ignore_notif_tag"
    const val INTENT_ACTION_IGNORE_TAG_NOTIFICATION_NID =
        "io.github.jd1378.otphelper.actions.ignore_notif_nid"
    const val INTENT_ACTION_IGNORE_NOTIFICATION_APP =
        "io.github.jd1378.otphelper.actions.ignore_notif_app"
    const val INTENT_ACTION_IGNORE_SMS_ORIGIN =
        "io.github.jd1378.otphelper.actions.ignore_sms_origin"
    const val INTENT_ACTION_SHOW_DETAILS = "io.github.jd1378.otphelper.actions.show_details"
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    if (context == null || intent == null) return

    val workData =
        workDataOf(
            "action" to intent.action,
            "cancel_notif_id" to intent.getIntExtra("cancel_notif_id", -1),
        )

    val workRequest = OneTimeWorkRequestBuilder<NotifActionWorker>().setInputData(workData).build()
    WorkManager.getInstance(context).enqueue(workRequest)
  }
}
