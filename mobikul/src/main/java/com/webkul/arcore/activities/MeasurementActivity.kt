package com.webkul.arcore.activities

import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.google.ar.core.*
import com.google.ar.core.Config.FocusMode.AUTO
import com.google.ar.core.Config.LightEstimationMode.ENVIRONMENTAL_HDR
import com.google.ar.core.exceptions.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.webkul.arcore.helper.CameraPermissionHelper
import com.webkul.mobikul.R
import com.webkul.mobikul.activities.BaseActivity
import com.webkul.mobikul.helpers.BundleKeysHelper
import com.webkul.mobikul.helpers.ToastHelper
import java.util.*

class MeasurementActivity : BaseActivity() {

    private var arFragment: ArFragment? = null
    private var mCubeRenderable: ModelRenderable? = null
    private var mSession: Session? = null
    private var planeDetected = false
    private var placeFirstNode = false
    private var placeSecondNode = false
    private var mUserRequestedInstall = false
    private var mMeasurement = ""
    private var mFirstNode: TransformableNode? = null
    private var mSecondNode: TransformableNode? = null
    private var mLineNode: Node? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkIsSupportedDeviceOrFinish(this)) {
            setContentView(R.layout.activity_measurement)

            arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?

            createArSession()
            mSession!!.configure(getSessionConfiguration())
            arFragment!!.arSceneView.setupSession(mSession)

            val btnClear = findViewById<AppCompatImageView>(R.id.btnClear)
            btnClear.setOnClickListener { onClear() }

            val btnCopy = findViewById<AppCompatImageView>(R.id.btnCopy)
            btnCopy.setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("measurement", mMeasurement)
                clipboard.setPrimaryClip(clip)
                ToastHelper.showToast(this, getString(R.string.copied_to_clipboard))
            }

            val btnAdd = findViewById<AppCompatImageView>(R.id.btnMark)
            btnAdd.setOnClickListener {
                if (planeDetected) {
                    if (!placeFirstNode && !placeSecondNode) {
                        placeFirstNode = true
                    } else {
                        placeSecondNode = true
                    }
                }
            }

            val btnDone = findViewById<AppCompatImageView>(R.id.btnDone)
            btnDone.setOnClickListener {
                val backResult = Intent()
                backResult.putExtra(BundleKeysHelper.BUNDLE_KEY_AR_MEASURED, mMeasurement)
                if (mMeasurement.isBlank()) {
                    setResult(RESULT_CANCELED, backResult)
                } else {
                    setResult(RESULT_OK, backResult)
                }
                finish()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                MaterialFactory.makeTransparentWithColor(this, Color(0f, 0f, 244f))
                        .thenAccept { material ->
                            val vector3 = Vector3(0.01f, 0.01f, 0.01f)
                            mCubeRenderable = ShapeFactory.makeCube(vector3, Vector3.zero(), material)
                            mCubeRenderable!!.isShadowCaster = false
                            mCubeRenderable!!.isShadowReceiver = false
                        }
            }

            arFragment!!.arSceneView.scene.addOnUpdateListener { this.onSceneUpdate(it) }
        } else {
            ToastHelper.showToast(this, getString(R.string.the_ar_feature_is_not_supported_by_your_device))
            this.finish()
        }
    }

    private fun getSessionConfiguration(): Config {
        val cfg = Config(mSession)
        cfg.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        cfg.focusMode = AUTO
        cfg.lightEstimationMode = ENVIRONMENTAL_HDR
        cfg.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
        return cfg
    }

    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e("error", "Sceneform requires Android N or later")
            return false
        }
        val openGlVersionString = (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < 3.1) {
            Log.e("error", "Sceneform requires OpenGL ES 3.1 later")
            return false
        }
        return true
    }

    private fun createArSession() {
        var exception: Exception? = null
        var message: String? = null
        try {
            if (mSession == null) {
                when (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> mUserRequestedInstall = true
                    ArCoreApk.InstallStatus.INSTALLED -> {
                    }
                }
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this)
                    return
                }
                mSession = Session(this)

                val filter = CameraConfigFilter(mSession)

                //                filter.setTargetFps(EnumSet.of(CameraConfig.TargetFps.TARGET_FPS_30));
                //                filter.setTargetFps(EnumSet.of(CameraConfig.TargetFps.TARGET_FPS_60));

                //                filter.setDepthSensorUsage(EnumSet.of(CameraConfig.DepthSensorUsage.
                //                        DO_NOT_USE));
                filter.setDepthSensorUsage(EnumSet.of(CameraConfig.DepthSensorUsage.REQUIRE_AND_USE))

                val cameraConfigList = mSession!!.getSupportedCameraConfigs(filter)

                if (!cameraConfigList.isEmpty()) {
                    mSession!!.cameraConfig = cameraConfigList[0]
                }
                Log.d(TAG, mSession!!.cameraConfig.depthSensorUsage.toString() + "")
            }
        } catch (e: UnavailableArcoreNotInstalledException) {
            message = "Please install ARCore"
            exception = e
        } catch (e: UnavailableUserDeclinedInstallationException) {
            message = "Please install ARCore"
            exception = e
        } catch (e: UnavailableApkTooOldException) {
            message = "Please update ARCore"
            exception = e
        } catch (e: UnavailableSdkTooOldException) {
            message = "Please update this app"
            exception = e
        } catch (e: UnavailableDeviceNotCompatibleException) {
            message = "This device does not support AR"
            exception = e
        } catch (e: Exception) {
            message = "Failed to create AR session"
            exception = e
        }

        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            Log.e(TAG, "Exception creating session", exception)
        }
    }

    private fun onSceneUpdate(frameTime: FrameTime) {
        if (mSession == null) {
            return
        }

        val frame = arFragment!!.arSceneView.arFrame ?: return

        if (frame.camera.trackingState == TrackingState.TRACKING) {
            if (!planeDetected && arFragment!!.arSceneView.session!!.getAllTrackables(Plane::class.java) != null && arFragment!!.arSceneView.session!!.getAllTrackables(Plane::class.java).size > 0) {
                planeDetected = true
                findViewById<View>(R.id.add_cursor).visibility = View.VISIBLE
            }

            val hitResults = arFragment!!.arSceneView.arFrame!!.hitTest(MotionEvent.obtain(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis() + 100,
                    MotionEvent.ACTION_UP,
                    (Resources.getSystem().displayMetrics.widthPixels / 2).toFloat(),
                    (Resources.getSystem().displayMetrics.heightPixels / 2).toFloat(),
                    0
            ))
            if (!hitResults.isEmpty()) {
                (findViewById<View>(R.id.add_cursor) as AppCompatImageView).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_indicator_enable))
                if (planeDetected && placeFirstNode) {
                    val anchor = arFragment!!.arSceneView.session!!.createAnchor(hitResults[0].hitPose)
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arFragment!!.arSceneView.scene)

                    if (mFirstNode == null) {
                        mFirstNode = TransformableNode(arFragment!!.transformationSystem)
                        mFirstNode!!.setParent(anchorNode)
                        mFirstNode!!.renderable = mCubeRenderable
                    } else {
                        if (mSecondNode == null) {
                            mSecondNode = TransformableNode(arFragment!!.transformationSystem)
                            mSecondNode!!.setParent(anchorNode)
                            mSecondNode!!.renderable = mCubeRenderable
                        } else {
                            if (!placeSecondNode) {
                                mSecondNode!!.setParent(anchorNode)
                                getDistanceBetweenVectorsInMeters(mFirstNode!!.worldPosition, mSecondNode!!.worldPosition)
                                drawLineBetweenNodes(mFirstNode!!.worldPosition, mSecondNode!!.worldPosition, anchorNode)
                            } else {
                                findViewById<View>(R.id.btnCopy).visibility = View.VISIBLE
                                placeFirstNode = false
                            }
                        }
                    }
                }
            } else {
                (findViewById<View>(R.id.add_cursor) as AppCompatImageView).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_indicator_disable))
            }
        }
    }

    private fun drawLineBetweenNodes(to: Vector3, from: Vector3, anchorNode: AnchorNode) {
        val difference = Vector3.subtract(to, from)
        val directionFromTopToBottom = difference.normalized()
        val rotationFromAToB = Quaternion.lookRotation(directionFromTopToBottom, Vector3.up())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            MaterialFactory.makeOpaqueWithColor(applicationContext, Color(0f, 255f, 244f))
                    .thenAccept { material ->
                        /* Then, create a rectangular prism, using ShapeFactory.makeCube() and use the difference vector
                                       to extend to the necessary length.  */
                        val model = ShapeFactory.makeCube(
                                Vector3(.01f, .01f, difference.length()),
                                Vector3.zero(), material)
                        /* Last, set the world rotation of the node to the rotation calculated earlier and set the world position to
                                       the midpoint between the given points . */
                        if (mLineNode == null) {
                            mLineNode = Node()
                        }
                        mLineNode!!.renderable = model
                        mLineNode!!.setParent(anchorNode)
                        mLineNode!!.worldPosition = Vector3.add(to, from).scaled(.5f)
                        mLineNode!!.worldRotation = rotationFromAToB
                    }
        }
    }

    private fun getDistanceBetweenVectorsInMeters(to: Vector3, from: Vector3) {
        // Compute the difference vector between the two hit locations.
        val dx = to.x - from.x
        val dy = to.y - from.y
        val dz = to.z - from.z

        // Compute the straight-line distance (distanceMeters)
        mMeasurement = String.format(Locale.UK, "%.2f", Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat() * 100)
        (findViewById<View>(R.id.distance) as TextView).text = "$mMeasurement cm"
    }

    private fun onClear() {
        val children = ArrayList(arFragment!!.arSceneView.scene.children)
        for (node in children) {
            if (node is AnchorNode) {
                if (node.anchor != null) {
                    node.anchor!!.detach()
                }
            }
        }
        placeFirstNode = false
        placeSecondNode = false
        mFirstNode = null
        mSecondNode = null
        mLineNode = null
    }

    companion object {
        private val TAG = "MeasurementActivity"
    }
}
