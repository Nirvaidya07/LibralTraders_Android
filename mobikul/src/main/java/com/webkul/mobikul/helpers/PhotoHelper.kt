/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2018 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.helpers

import android.content.Context
import android.hardware.Camera
import android.os.Environment
import android.util.Log
import com.webkul.mobikul.R
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PhotoHelper(val context: Context) : Camera.PictureCallback {

    override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
        val pictureFileDir = getDir()

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            Log.d("DEBUG", "Can't create directory to save image.")
            ToastHelper.showToast(context, context.getString(R.string.cant_create_directory_to_save_image))
            return
        }

        val dateFormat = SimpleDateFormat("yyyymmddhhmmss", Locale.US)
        val date = dateFormat.format(Date())
        val photoFile = "Picture_$date.jpg"

        val filename = pictureFileDir.getPath() + File.separator + photoFile

        val pictureFile = File(filename)

        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
            ToastHelper.showToast(context, String.format(context.getString(R.string.new_image_saved_x), photoFile))
        } catch (error: Exception) {
            Log.d("DEBUG", "File" + filename + "not saved: " + error.message)
            ToastHelper.showToast(context, context.getString(R.string.image_could_not_be_saved))
        }
    }

    private fun getDir(): File {
        val sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File(sdDir, "CameraAPIDemo")
    }
}