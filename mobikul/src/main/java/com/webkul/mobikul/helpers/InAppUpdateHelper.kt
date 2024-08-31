package com.webkul.mobikul.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.helpers.ConstantsHelper.APP_UPDATE_REQUEST_CODE

class InAppUpdateHelper(val mContext: Context) {
    //1.Check update staleness
   //2.Check update priority
    lateinit var updateManager: AppUpdateManager

    fun checkForAppUpdate() {
        updateManager = AppUpdateManagerFactory.create(mContext)

        updateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                try {
                    val installType = when {
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> AppUpdateType.IMMEDIATE
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> AppUpdateType.FLEXIBLE
                        else -> null
                    }
                    if (installType == AppUpdateType.FLEXIBLE) updateManager.registerListener(installStateUpdatedListener)

                    installType?.let {
                        updateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                it,
                                mContext as BaseActivity,
                                APP_UPDATE_REQUEST_CODE)
                    }
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                updateSnackBarForComplete()
            } else {
                Log.e("TAG", "checkForAppUpdateAvailability: something else")
            }
        }
    }

    private var installStateUpdatedListener: InstallStateUpdatedListener = object : InstallStateUpdatedListener {
        override fun onStateUpdate(state: InstallState) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                updateSnackBarForComplete()
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                updateManager.unregisterListener(this)
            } else {
                Log.i("TAG", "InstallStateUpdatedListener: state: " + state.installStatus())
            }
        }
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(mContext,
                        mContext.getString(R.string.update_failed),
                        Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }


    private fun updateSnackBarForComplete() {
        val snackbar = Snackbar.make((mContext as BaseActivity).findViewById(R.id.main_container),
                mContext.getString(R.string.new_version_downloaded),
                Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(mContext.getString(R.string.restart)) { updateManager.completeUpdate() }
        snackbar.setActionTextColor(ContextCompat.getColor(mContext, R.color.colorAccent))
        snackbar.show()
    }

    fun checkUpdateFinished() {
        updateManager
                .appUpdateInfo
                .addOnSuccessListener { appUpdateInfo ->
                    // If the update is downloaded but not installed,
                    // notify the user to complete the update.
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        updateSnackBarForComplete()
                    }
                    //Check if Immediate update is required
                    try {
                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                            // If an in-app update is already running, resume the update.
                            updateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    AppUpdateType.IMMEDIATE,
                                    mContext as BaseActivity,
                                    APP_UPDATE_REQUEST_CODE)
                        }
                    } catch (e: SendIntentException) {
                        e.printStackTrace()
                    }
                }
    }
}