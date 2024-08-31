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
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.Menu
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.libraltraders.android.R
import com.webkul.mobikul.activities.BaseActivity
import com.libraltraders.android.databinding.ActivityArBinding
import com.webkul.mobikul.helpers.ApplicationConstants
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_AR_MODEL_URL
import com.webkul.mobikul.helpers.BundleKeysHelper.BUNDLE_KEY_PRODUCT_NAME
import com.webkul.mobikul.helpers.ConstantsHelper.RC_AR
import com.webkul.mobikul.helpers.ToastHelper
import io.github.inflationx.calligraphy3.CalligraphyTypefaceSpan
import io.github.inflationx.calligraphy3.TypefaceUtils
import java.util.concurrent.CompletableFuture

private const val MIN_OPENGL_VERSION = 3.1

class ArActivity : BaseActivity() {

    private lateinit var mContentViewBinding: ActivityArBinding

    private var arFragment: ArFragment? = null
    private var objectRenderable: ModelRenderable? = null
    private var arModel: String? = null
    private var mModelStateSnackBar: Snackbar? = null
    private lateinit var mModelCompletableFuture: CompletableFuture<Void>
    var anchorNode: AnchorNode? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arModel = intent.getStringExtra(BUNDLE_KEY_AR_MODEL_URL)
        if (checkIsSupportedDeviceOrFinish(this)) {
            mContentViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_ar)
            initSupportActionBar()
            startInitialization()
        } else {
            ToastHelper.showToast(this, getString(R.string.the_ar_feature_is_not_supported_by_your_device))
            this.finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun initSupportActionBar() {
        setSupportActionBar(mContentViewBinding.toolbar)
        val title = SpannableString(intent.getStringExtra(BUNDLE_KEY_PRODUCT_NAME) ?: "")
        title.setSpan(CalligraphyTypefaceSpan(TypefaceUtils.load(assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD)), 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        supportActionBar?.title = title
        supportActionBar?.elevation = 4f
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun startInitialization() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {

                        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment


                        Toast.makeText(this@ArActivity, getString(R.string.downloading_model), Toast.LENGTH_SHORT).show()

                        // Init renderable
                        loadModel()

                         // Set tap listener
                        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
                            val anchor = hitResult.createAnchor()
                            if (anchorNode == null) {
                                anchorNode = AnchorNode(anchor)
                                anchorNode?.setParent(arFragment!!.arSceneView.scene)
                                createModel()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, RC_AR)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.d("TAG", e.printStackTrace().toString() + e.message.toString())
        }
    }

    private fun createModel() {
        try {
            if (anchorNode != null) {
                val node = TransformableNode(arFragment!!.transformationSystem)
                node.scaleController.maxScale = 1.0f
                node.scaleController.minScale = 0.01f
                node.scaleController.sensitivity = 0.1f
                node.setParent(anchorNode)
                node.renderable = objectRenderable

                node.select()
                mModelStateSnackBar = Snackbar.make(mContentViewBinding.arLayout, getString(R.string.model_ready), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.dismiss)) {
                    mModelStateSnackBar?.dismiss()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this@ArActivity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun loadModel() {
        try {
            ModelRenderable.builder()
                    .setSource(this,
                            RenderableSource.builder().setSource(
                                    this,
                                    Uri.parse(arModel),
                                    RenderableSource.SourceType.GLB)
                                    .setScale(0.75f)
                                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                                    .build())
                    .setRegistryId(arModel)
                    .build()
                    .thenAccept { renderable: ModelRenderable ->
                        objectRenderable = renderable
                        Toast.makeText(this@ArActivity, getString(R.string.model_ready), Toast.LENGTH_SHORT).show()

                    }
                    .exceptionally { throwable: Throwable? ->
                        Log.i("Model", "cant load")
                        mModelStateSnackBar = Snackbar.make(mContentViewBinding.arLayout, getString(R.string.model_error), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.try_again)) {
                            mModelStateSnackBar?.dismiss()
                        }
                        null
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun checkIfModelReady() {
        if (mModelCompletableFuture.isDone && !isDestroyed) {
            if (mModelCompletableFuture.isCompletedExceptionally || mModelCompletableFuture.isCancelled) {
                mModelStateSnackBar = Snackbar.make(mContentViewBinding.arLayout, getString(R.string.model_error), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.try_again)) {
                    loadModel()
                    mModelStateSnackBar?.dismiss()
                }
                mModelStateSnackBar?.view?.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
                mModelStateSnackBar?.view?.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.setTextColor(ContextCompat.getColor(this, R.color.text_color_primary))
                mModelStateSnackBar?.show()
            } else {
                mModelStateSnackBar = Snackbar.make(mContentViewBinding.arLayout, getString(R.string.model_ready), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.dismiss)) {
                    mModelStateSnackBar?.dismiss()
                }
                mModelStateSnackBar?.view?.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
                mModelStateSnackBar?.view?.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.setTextColor(ContextCompat.getColor(this, R.color.text_color_primary))
                mModelStateSnackBar?.show()
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({ this.checkIfModelReady() }, 500)
        }
    }

    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e("error", "Sceneform requires Android N or later")
            return false
        }
        val openGlVersionString = (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e("error", "Sceneform requires OpenGL ES 3.1 later")
            return false
        }
        return true
    }


    /*private fun createArSession() {
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
            }
        } catch (e: UnavailableArcoreNotInstalledException) {
            message = getString(R.string.ar_core_install_error)
            exception = e
        } catch (e: UnavailableUserDeclinedInstallationException) {
            message = getString(R.string.ar_core_install_error)
            exception = e
        } catch (e: UnavailableApkTooOldException) {
            message = getString(R.string.ar_core_update_error)
            exception = e
        } catch (e: UnavailableSdkTooOldException) {
            message = getString(R.string.ar_core_update_error)
            exception = e
        } catch (e: UnavailableDeviceNotCompatibleException) {
            message = getString(R.string.the_ar_feature_is_not_supported_by_your_device)
            exception = e
        } catch (e: Exception) {
            message = getString(R.string.ar_core_session_error)
            exception = e
        }

        if (message != null) {
            ToastHelper.showToast(this, message)
            exception?.printStackTrace()
        }
    }
*/
    override fun onDestroy() {
        super.onDestroy()
        if (::mModelCompletableFuture.isInitialized)
            mModelCompletableFuture.cancel(true)
    }
}