package io.github.jd1378.otphelper.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri

class AutostartHelper {
  companion object {
    private val POWER_MANAGER_INTENTS =
        listOf(
            Intent()
                .setComponent(
                    ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            Intent()
                .setComponent(
                    ComponentName(
                        "com.letv.android.letvsafe",
                        "com.letv.android.letvsafe.AutobootManageActivity")),
            Intent()
                .setComponent(
                    ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            Intent()
                .setComponent(
                    ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            Intent()
                .setComponent(
                    ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            Intent()
                .setComponent(
                    ComponentName(
                        "com.oppo.safe",
                        "com.oppo.safe.permission.startup.StartupAppListActivity")),
            Intent()
                .setComponent(
                    ComponentName(
                        "com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            Intent()
                .setComponent(
                    ComponentName(
                        "com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            Intent()
                .setComponent(
                    ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            Intent()
                .setComponent(
                    ComponentName(
                        "com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity"))
                .setData(Uri.parse("mobilemanager://function/entry/AutoStart")))

    fun openAutostartSettings(context: Context) {
      for (intent in POWER_MANAGER_INTENTS) {
        if (ActivityHelper.isCallable(context, intent)) {
          context.startActivity(intent)
          break
        }
      }
    }

    fun hasAutostartSettings(context: Context): Boolean {
      for (intent in POWER_MANAGER_INTENTS) {
        if (ActivityHelper.isCallable(context, intent)) {
          return true
        }
      }
      return false
    }
  }
}
