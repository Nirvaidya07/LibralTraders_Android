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

package com.webkul.arcore.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.graphics.PointF
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.webkul.arcore.helper.CameraPreview
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.databinding.ActivityCameraWithImageBinding
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_IMAGE
import com.webkul.mobikul.helpers.ImageHelper
import com.webkul.mobikul.helpers.ToastHelper


class CameraWithImageActivity : BaseActivity(), View.OnTouchListener {

    private var mBinding: ActivityCameraWithImageBinding? = null
    private var mCamera: Camera? = null

    // we can be in one of these 3 states
    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2

    // these matrices will be used to move and zoom image
    private val matrix = Matrix()
    private val savedMatrix = Matrix()
    private var mode = NONE

    // remember some things for zooming
    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f
    private var d = 0f
    private var lastEvent: FloatArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_with_image)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera_with_image)

        if (intent.hasExtra(BUNDLE_KEY_PRODUCT_IMAGE) && !intent.getStringExtra(BUNDLE_KEY_PRODUCT_IMAGE).isNullOrBlank()) {
            startInitialization()
        } else {
            ToastHelper.showToast(this, getString(R.string.invalid_image_link))
            finish()
        }
    }

    private fun startInitialization() {
        // Create an instance of Camera
        mCamera = getCameraInstance()

        if (mCamera != null) {
            // Create our Preview view and set it as the content of our activity.
            mBinding!!.cameraPreview.addView(CameraPreview(this, mCamera!!))

            ImageHelper.load(mBinding!!.imageView, intent.getStringExtra(BUNDLE_KEY_PRODUCT_IMAGE), null)
            mBinding!!.imageView.setOnTouchListener(this)
        }
    }

    fun getCameraInstance(): Camera? {
        var c: Camera? = null
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                c = Camera.open() // attempt to get a Camera instance
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    requestPermissions(permissions, 123)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return c // returns null if camera is unavailable
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
// handle touch events here
        val view = v as ImageView
        when (event!!.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                start.set(event.x, event.y)
                mode = DRAG
                lastEvent = null
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = ZOOM
                }
                lastEvent = FloatArray(4)
                lastEvent!![0] = event.getX(0)
                lastEvent!![1] = event.getX(1)
                lastEvent!![2] = event.getY(0)
                lastEvent!![3] = event.getY(1)
                d = rotation(event)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                if (event.getPointerCount() == 1) {
                    matrix.set(savedMatrix)
                    val dx = event.getX() - start.x
                    val dy = event.getY() - start.y
                    matrix.postTranslate(dx, dy)
                }
            } else if (mode == ZOOM) {
                val newDist = spacing(event)
                if (newDist > 10f) {
                    matrix.set(savedMatrix)
                    val scale = newDist / oldDist
                    matrix.postScale(scale, scale, mid.x, mid.y)
                }
                if (lastEvent != null && event.getPointerCount() == 2) {
                    val r = rotation(event) - d
                    val values = FloatArray(9)
                    matrix.getValues(values)
                    val tx = values[2]
                    val ty = values[5]
                    val sx = values[0]
                    val xc = (event.getX(0) + event.getX(1)) / 2 * sx
                    val yc = (event.getY(0) + event.getY(1)) / 2 * sx
                    matrix.postRotate(r, (event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2)
                }
            }
        }

        view.imageMatrix = matrix
        return true
    }

    /**
     * Determine the space between the first two fingers
     */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private fun rotation(event: MotionEvent): Float {
        val delta_x = (event.getX(0) - event.getX(1)).toDouble()
        val delta_y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = Math.atan2(delta_y, delta_x)
        return Math.toDegrees(radians).toFloat()
    }

    override fun onPause() {
        super.onPause()
        mCamera?.release()          // release the camera immediately on pause event
    }

    fun onClickTakePicture(view: View) {
//        mCamera?.takePicture(null, null, PhotoHelper(getApplicationContext()))
    }
}