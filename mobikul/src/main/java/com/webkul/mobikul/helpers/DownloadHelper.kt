/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2019 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.helpers

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.webkul.mobikul.R

class DownloadHelper {

    companion object {
        fun downloadFile(context: Context?, url: String?, fileName: String?, mimeType: String?) {
            try {
                val request = DownloadManager.Request(Uri.parse(url))
                request.setDescription(context!!.resources.getString(R.string.download_started))
                request.setTitle(fileName)
                request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                request.addRequestHeader("authKey", AuthKeyHelper.getInstance().authKey)
                request.setMimeType(mimeType)
                val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                manager.enqueue(request)

                ToastHelper.showToast(context, context.getString(R.string.download_started), Toast.LENGTH_LONG)
            } catch (e: Exception) {
                e.printStackTrace()
                ToastHelper.showToast(context!!, context.getString(R.string.something_went_wrong), Toast.LENGTH_LONG)
            }
        }
    }
}