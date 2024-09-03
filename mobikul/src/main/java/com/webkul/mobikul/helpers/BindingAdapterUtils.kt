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

import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.*
import androidx.appcompat.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import androidx.webkit.WebSettingsCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import com.libraltraders.android.R
import com.webkul.mobikul.helpers.ApplicationConstants.BASE_URL
import com.webkul.mobikul.helpers.ApplicationConstants.ENABLE_DYNAMIC_THEME_COLOR
import com.webkul.mobikul.helpers.ConstantsHelper.ORDER_STATUS_CANCELLED
import com.webkul.mobikul.helpers.ConstantsHelper.ORDER_STATUS_CLOSED
import com.webkul.mobikul.helpers.ConstantsHelper.ORDER_STATUS_COMPLETE
import com.webkul.mobikul.helpers.ConstantsHelper.ORDER_STATUS_DOWNLOADED
import com.webkul.mobikul.helpers.ConstantsHelper.ORDER_STATUS_HOLD
import com.webkul.mobikul.helpers.ConstantsHelper.ORDER_STATUS_NEW
import com.webkul.mobikul.helpers.ConstantsHelper.ORDER_STATUS_PENDING
import com.webkul.mobikul.helpers.ConstantsHelper.ORDER_STATUS_PROCESSING
import com.webkul.mobikul.helpers.Utils.Companion.setToolbarThemeColor
import com.webkul.mobikul.models.product.SwatchData
import java.util.*

class BindingAdapterUtils {

    companion object {

        @JvmStatic
        @BindingAdapter("layout_width")
        fun setLayoutWidth(view: View, width: Int) {
            val layoutParams = view.layoutParams
            layoutParams.width = width
            view.layoutParams = layoutParams
        }

        @JvmStatic
        @BindingAdapter("layout_height")
        fun setLayoutHeight(view: View, height: Int) {
            val layoutParams = view.layoutParams
            layoutParams.height = height
            view.layoutParams = layoutParams
        }

        @JvmStatic
        @BindingAdapter(value = ["imageUrl", "placeholder"], requireAll = false)
        fun setImageUrl(view: ImageView, imageUrl: String?, placeholder: String?) {
            ImageHelper.load(view, imageUrl, placeholder)
        }

        @JvmStatic
        @BindingAdapter("tiledBackgroundImageUrl")
        fun setTiledBackgroundImageUrl(view: ImageView, imageUrl: String?) {
            if (!imageUrl.isNullOrBlank()) {
                Glide.with(view.context).asBitmap().load(imageUrl).into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val bitmapDrawable = BitmapDrawable(view.resources, resource)
                        bitmapDrawable.tileModeX = Shader.TileMode.REPEAT
                        bitmapDrawable.tileModeY = Shader.TileMode.REPEAT
                        view.background = bitmapDrawable
                    }
                })
            }
        }

        @JvmStatic
        @BindingAdapter("backgroundColor")
        fun setBackgroundColor(view: View, colorHexCode: String?) {
            try {
                if (colorHexCode != null && colorHexCode.isNotBlank())
                    view.setBackgroundColor(Color.parseColor(colorHexCode))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        @BindingAdapter("srcCompat")
        fun setSrcCompat(view: ImageView, drawable: Drawable) {
            view.setImageDrawable(drawable)
        }

        @JvmStatic
        @BindingAdapter("isInWishList")
        fun setIsInWishList(view: LottieAnimationView, isInWishList: Boolean) {
            if (isInWishList) view.progress = 1f else view.progress = 0f
        }

        @JvmStatic
        @BindingAdapter("drawableId")
        fun setImageByDrawableId(view: ImageView, drawableId: Int) {
            if (drawableId != 0)
                view.setImageDrawable(ContextCompat.getDrawable(view.context, drawableId))
        }

        /*
        * 0=View background color and text color
        * 1 = Tool bar color
        * 2 = CardView background color
        * 3 = Collapsing toolbar color
        * 4 == App theme Image drawable color
        * 5 == App theme text color
        * 6 == App theme background color
        * 7 == Button Drawable stock color and text color
        * 8 == Inverse button
        * 9 == View Drawable background and Drawable color
        * 10== Button Text color
        * 14== ProgressBar  color
        * */
        @JvmStatic
        @BindingAdapter("appTheme")
        fun setAppThem(view: View, type: Int) {
            if (ENABLE_DYNAMIC_THEME_COLOR) {
                try {
                when (type) {
                    0 -> {
                        val drawable: Drawable? = view.background

                        if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                            if (drawable == null) {
                                view.background = ColorDrawable(Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context)))
                            } else {
                                val wrappedDrawable: Drawable = DrawableCompat.wrap(drawable)
                                DrawableCompat.setTint(wrappedDrawable, Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context)))
                                view.background = wrappedDrawable
                            }
                        } else if (drawable == null) {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.shape_rect_round_cnr_accent_bg_accent_border_1dp)
                        }
                        when (view) {
                            is AppCompatButton -> {
                                if (AppSharedPref.getButtonTextColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getButtonTextColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, android.R.color.white))
                                }
                            }
                            is AppCompatTextView -> {
                                if (AppSharedPref.getButtonTextColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getButtonTextColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, android.R.color.white))
                                }
                            }
                            is Button -> {
                                if (AppSharedPref.getButtonTextColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getButtonTextColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, android.R.color.white))
                                }
                            }
                            is TextView -> {
                                if (AppSharedPref.getButtonTextColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getButtonTextColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, android.R.color.white))
                                }
                            }
                        }
                    }
                    1 -> {
                        setToolbarThemeColor(view as Toolbar)
                    }
                    2 -> {
                        (view as MaterialCardView).setCardBackgroundColor(Color.parseColor(AppSharedPref.getAppThemeColor(view.context)))
                    }
                    3 -> {
                        val foreground = if (AppSharedPref.getAppThemeTextColor(view.context).isNotEmpty()) {
                            Color.parseColor(AppSharedPref.getAppThemeTextColor(view.context))
                        } else {
                            ContextCompat.getColor(view.context, R.color.actionBarItemsColor)
                        }
                        if (view is CollapsingToolbarLayout && AppSharedPref.getAppThemeColor(view.context).isNotEmpty()) {
                            view.contentScrim = ColorDrawable(Color.parseColor(AppSharedPref.getAppThemeColor(view.context)))
                            view.setCollapsedTitleTextColor(foreground)
                            view.setExpandedTitleColor(foreground)
                        }


                        view.findViewById<Toolbar>(R.id.toolbar).setTitleTextColor(foreground)
                        view.findViewById<Toolbar>(R.id.toolbar).setSubtitleTextColor(foreground)

                        val colorFilter = PorterDuffColorFilter(foreground, PorterDuff.Mode.MULTIPLY)

                        // Overflow icon
                        val overflowIcon: Drawable? =view.findViewById<Toolbar>(R.id.toolbar).overflowIcon
                        if (overflowIcon != null) {
                            overflowIcon.colorFilter = colorFilter
                            view.findViewById<Toolbar>(R.id.toolbar).overflowIcon = overflowIcon
                        }

                        //Overflow navigation icon
                        val navigationIcon: Drawable? = view.findViewById<Toolbar>(R.id.toolbar).navigationIcon
                        if (navigationIcon != null) {
                            navigationIcon.colorFilter = colorFilter
                            view.findViewById<Toolbar>(R.id.toolbar).navigationIcon = navigationIcon
                        }
                    }
                    4 -> {
                        if (view is ImageView) {
                            val foreground = if (AppSharedPref.getAppThemeTextColor(view.context).isNotEmpty()) {
                                Color.parseColor(AppSharedPref.getAppThemeTextColor(view.context))
                            } else {
                                ContextCompat.getColor(view.context, R.color.actionBarItemsColor)
                            }
                            view.setColorFilter(foreground, PorterDuff.Mode.SRC_ATOP)
                        }
                    }
                    5 -> {
                        val foreground = if (AppSharedPref.getAppThemeTextColor(view.context).isNotEmpty()) {
                            Color.parseColor(AppSharedPref.getAppThemeTextColor(view.context))
                        } else {
                            ContextCompat.getColor(view.context, R.color.actionBarItemsColor)
                        }
                        if (view is AppCompatEditText)
                            view.setHintTextColor(foreground)
                        if (view is TextView) {
                            view.setTextColor(foreground)
                        } else if (view is EditText) {
                            view.setTextColor(foreground)
                        }
                    }
                    6 -> {
                        val background = if (AppSharedPref.getAppThemeColor(view.context).isNotEmpty()) {
                            Color.parseColor(AppSharedPref.getAppThemeColor(view.context))
                        } else {
                            ContextCompat.getColor(view.context, R.color.colorPrimary)
                        }
                        view.background = ColorDrawable(background)
                    }
                    7 -> {
                        Log.d("test", "7")

                        val background = if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                            Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context))
                        } else {
                            ContextCompat.getColor(view.context, R.color.colorAccent)
                        }

                        val gd = GradientDrawable()
                        gd.setColor(ContextCompat.getColor(view.context, R.color.material_background))
                        gd.cornerRadius = 0f
                        gd.setStroke(2, background)
                        view.background = gd


                        when (view) {
                            is AppCompatButton -> {
                                if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, R.color.text_color_primary))
                                }
                            }
                            is AppCompatTextView -> {
                                if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, R.color.text_color_primary))
                                }
                            }
                            is Button -> {
                                if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, R.color.text_color_primary))
                                }
                            }
                            is TextView -> {
                                if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, R.color.text_color_primary))
                                }
                            }
                        }
                    }
                    8 -> {
                        if (AppSharedPref.getButtonTextColor(view.context).isNotEmpty()) {
                            view.background = ColorDrawable(Color.parseColor(AppSharedPref.getButtonTextColor(view.context)))
                        } else {
                            view.background = ContextCompat.getDrawable(view.context, R.color.color_whiteBlack)
                        }
                        when (view) {
                            is AppCompatButton -> {
                                if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, R.color.text_color_primary))
                                }
                            }
                            is AppCompatTextView -> {
                                if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, R.color.text_color_primary))
                                }
                            }
                            is Button -> {
                                if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, R.color.text_color_primary))
                                }
                            }
                            is TextView -> {
                                if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, R.color.text_color_primary))
                                }
                            }
                        }
                    }
                    9 -> {
                        Log.d("test", "9")
                        if (view is ImageView) {
                            val foreground = if (AppSharedPref.getButtonTextColor(view.context).isNotEmpty()) {
                                Color.parseColor(AppSharedPref.getButtonTextColor(view.context))
                            } else {
                                ContextCompat.getColor(view.context, R.color.color_whiteBlack)
                            }
                            view.setColorFilter(foreground, PorterDuff.Mode.SRC_ATOP)

                            val background = if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context))
                            } else {
                                ContextCompat.getColor(view.context, R.color.colorAccent)
                            }
                            view.background = ColorDrawable(background)
                        }else if(view is AppCompatImageView){
                            val foreground = if (AppSharedPref.getButtonTextColor(view.context).isNotEmpty()) {
                                Color.parseColor(AppSharedPref.getButtonTextColor(view.context))
                            } else {
                                ContextCompat.getColor(view.context, R.color.color_whiteBlack)
                            }
                            view.setColorFilter(foreground, PorterDuff.Mode.SRC_ATOP)

                            val background = if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context))
                            } else {
                                ContextCompat.getColor(view.context, R.color.colorAccent)
                            }
                            view.background = ColorDrawable(background)
                        }
                    }
                    10 -> {
                        val foreground = if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                            Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context))
                        } else {
                            ContextCompat.getColor(view.context, R.color.colorAccent)
                        }
                        when (view) {
                            is AppCompatEditText -> view.setHintTextColor(foreground)
                            is EditText -> view.setHintTextColor(foreground)
                        }
                        when (view) {
                            is TextView -> {
                                view.setTextColor(foreground)
                            }
                            is AppCompatTextView -> view.setTextColor(foreground)
                            is EditText -> {
                                view.setTextColor(foreground)
                            }
                            is AppCompatEditText -> {
                                view.setTextColor(foreground)
                            }
                        }
                    }
                    11 -> {
                        if (view is ImageView) {
                            val foreground = if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context))
                            } else {
                                ContextCompat.getColor(view.context, R.color.colorAccent)
                            }
                            view.setColorFilter(foreground, PorterDuff.Mode.SRC_ATOP)
                        }  else if (view is AppCompatImageView) {
                            val foreground = if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context))
                            } else {
                                ContextCompat.getColor(view.context, R.color.colorAccent)
                            }
                            view.setColorFilter(foreground, PorterDuff.Mode.SRC_ATOP)
                        }
                    }
                    12 -> {
                        when (view) {
                            is ImageView ->{
                                   if (AppSharedPref.getButtonTextColor(view.context).isNotEmpty()) {
                                    val drawable: Drawable? = view.background
                                    if (drawable == null) {
                                        view.background = ColorDrawable(Color.parseColor(AppSharedPref.getButtonTextColor(view.context)))
                                    } else {
                                        val wrappedDrawable: Drawable = DrawableCompat.wrap(drawable)
                                        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(AppSharedPref.getButtonTextColor(view.context)))
                                        view.background = wrappedDrawable
                                    }
                            }
                            }
                                is AppCompatImageView -> {
                                if (AppSharedPref.getButtonTextColor(view.context).isNotEmpty()) {
                                    val drawable: Drawable? = view.background
                                    if (drawable == null) {
                                        view.background = ColorDrawable(Color.parseColor(AppSharedPref.getButtonTextColor(view.context)))
                                    } else {
                                        val wrappedDrawable: Drawable = DrawableCompat.wrap(drawable)
                                        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(AppSharedPref.getButtonTextColor(view.context)))
                                        view.background = wrappedDrawable
                                    }
                                }
                            }
                            is AppCompatTextView -> {
                                val foreground = if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                    Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context))
                                } else {
                                    ContextCompat.getColor(view.context, R.color.text_color_primary)
                                }
                                view.setTextColor(foreground)
                                for (drawables in view.compoundDrawables) {
                                    if (drawables != null) {
                                        //drawables.colorFilter = PorterDuffColorFilter(ColorUtils.setAlphaComponent(foreground, 180), PorterDuff.Mode.SRC_ATOP)
                                        DrawableCompat.setTint(drawables, foreground)
                                    }
                                }

                                   for (drawables in view.compoundDrawablesRelative) {
                                        if (drawables != null) {
                                      //      drawables.colorFilter = PorterDuffColorFilter(ColorUtils.setAlphaComponent(foreground, 180), PorterDuff.Mode.SRC_ATOP)
                                            DrawableCompat.setTint(drawables, foreground)
                                        }
                                    }
                            }
                            is TextView -> {
                                val foreground = if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                                    Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context))
                                } else {
                                    ContextCompat.getColor(view.context, R.color.text_color_primary)
                                }
                                view.setTextColor(foreground)
                                for (drawables in view.compoundDrawables) {
                                    if (drawables != null) {
                                    //    drawables.colorFilter = PorterDuffColorFilter(ColorUtils.setAlphaComponent(foreground, 180), PorterDuff.Mode.SRC_ATOP)

                                        DrawableCompat.setTint(drawables, foreground)
                                    }
                                }

                                    for (drawables in view.compoundDrawablesRelative) {
                                        if (drawables != null) {
                                          //  drawables.colorFilter = PorterDuffColorFilter(ColorUtils.setAlphaComponent(foreground, 180), PorterDuff.Mode.SRC_ATOP)
                                          /*  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                                Log.d("colorFilter",view.text.toString() +"::::::::"+drawables.colorFilter.toString())
                                            }*/
                                            DrawableCompat.setTint(drawables, foreground)
                                        }
                                    }
                            }

                        }

                    }
                    14 -> {
                        if (view is ProgressBar) {
                            val background = if (AppSharedPref.getAppThemeTextColor(view.context).isNotEmpty()) {
                                Color.parseColor(AppSharedPref.getAppThemeTextColor(view.context))
                            } else {
                                ContextCompat.getColor(view.context, R.color.colorAccent)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                view.indeterminateDrawable.colorFilter = BlendModeColorFilter(background, BlendMode.SRC_ATOP)
                            } else {
                                view.indeterminateDrawable.setColorFilter(background, PorterDuff.Mode.SRC_ATOP)
                            }
                        }
                    }
                   15 -> {
                        val drawable: Drawable? = view.background

                        if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                            if (drawable == null) {
                                view.background = ColorDrawable(Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context)))
                            } else {
                                val wrappedDrawable: Drawable = DrawableCompat.wrap(drawable)
                                DrawableCompat.setTint(wrappedDrawable, Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context)))
                                view.background = wrappedDrawable
                            }
                        } else if (drawable == null) {
                            view.background = ContextCompat.getDrawable(view.context, R.drawable.shape_rect_round_cnr_accent_bg_accent_border_1dp)
                        }
                        when (view) {
                            is AppCompatButton -> {
                                val foreground = if (AppSharedPref.getAppThemeColor(view.context).isNotEmpty()) {
                                    Color.parseColor(AppSharedPref.getAppThemeColor(view.context))
                                } else {
                                    ContextCompat.getColor(view.context, R.color.text_color_primary)
                                }
                                view.setTextColor(foreground)
                                for (drawables in view.compoundDrawables) {
                                    if (drawables != null) {
                                        //drawables.colorFilter = PorterDuffColorFilter(ColorUtils.setAlphaComponent(foreground, 180), PorterDuff.Mode.SRC_ATOP)
                                        DrawableCompat.setTint(drawables, foreground)
                                    }
                                }

                                for (drawables in view.compoundDrawablesRelative) {
                                    if (drawables != null) {
                                        //      drawables.colorFilter = PorterDuffColorFilter(ColorUtils.setAlphaComponent(foreground, 180), PorterDuff.Mode.SRC_ATOP)
                                        DrawableCompat.setTint(drawables, foreground)
                                    }
                                }
                                if (AppSharedPref.getAppThemeColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getAppThemeColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, android.R.color.white))
                                }
                            }
                            is AppCompatTextView -> {
                                val foreground = if (AppSharedPref.getAppThemeColor(view.context).isNotEmpty()) {
                                    Color.parseColor(AppSharedPref.getAppThemeColor(view.context))
                                } else {
                                    ContextCompat.getColor(view.context, R.color.text_color_primary)
                                }
                                view.setTextColor(foreground)
                                for (drawables in view.compoundDrawables) {
                                    if (drawables != null) {
                                        //drawables.colorFilter = PorterDuffColorFilter(ColorUtils.setAlphaComponent(foreground, 180), PorterDuff.Mode.SRC_ATOP)
                                        DrawableCompat.setTint(drawables, foreground)
                                    }
                                }

                                for (drawables in view.compoundDrawablesRelative) {
                                    if (drawables != null) {
                                        //      drawables.colorFilter = PorterDuffColorFilter(ColorUtils.setAlphaComponent(foreground, 180), PorterDuff.Mode.SRC_ATOP)
                                        DrawableCompat.setTint(drawables, foreground)
                                    }
                                }
                                if (AppSharedPref.getAppThemeColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getAppThemeColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, android.R.color.white))
                                }
                            }
                            is Button -> {
                                val foreground = if (AppSharedPref.getAppThemeColor(view.context).isNotEmpty()) {
                                    Color.parseColor(AppSharedPref.getAppThemeColor(view.context))
                                } else {
                                    ContextCompat.getColor(view.context, R.color.text_color_primary)
                                }
                                view.setTextColor(foreground)
                                for (drawables in view.compoundDrawables) {
                                    if (drawables != null) {
                                        //drawables.colorFilter = PorterDuffColorFilter(ColorUtils.setAlphaComponent(foreground, 180), PorterDuff.Mode.SRC_ATOP)
                                        DrawableCompat.setTint(drawables, foreground)
                                    }
                                }

                                for (drawables in view.compoundDrawablesRelative) {
                                    if (drawables != null) {
                                        //      drawables.colorFilter = PorterDuffColorFilter(ColorUtils.setAlphaComponent(foreground, 180), PorterDuff.Mode.SRC_ATOP)
                                        DrawableCompat.setTint(drawables, foreground)
                                    }
                                }
                                if (AppSharedPref.getAppThemeColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getAppThemeColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, android.R.color.white))
                                }
                            }
                            is TextView -> {
                                val foreground = if (AppSharedPref.getAppThemeColor(view.context).isNotEmpty()) {
                                    Color.parseColor(AppSharedPref.getAppThemeColor(view.context))
                                } else {
                                    ContextCompat.getColor(view.context, R.color.text_color_primary)
                                }
                                view.setTextColor(foreground)
                                for (drawables in view.compoundDrawables) {
                                    if (drawables != null) {
                                        //drawables.colorFilter = PorterDuffColorFilter(ColorUtils.setAlphaComponent(foreground, 180), PorterDuff.Mode.SRC_ATOP)
                                        DrawableCompat.setTint(drawables, foreground)
                                    }
                                }

                                for (drawables in view.compoundDrawablesRelative) {
                                    if (drawables != null) {
                                        //      drawables.colorFilter = PorterDuffColorFilter(ColorUtils.setAlphaComponent(foreground, 180), PorterDuff.Mode.SRC_ATOP)
                                        DrawableCompat.setTint(drawables, foreground)
                                    }
                                }
                                if (AppSharedPref.getAppThemeColor(view.context).isNotEmpty()) {
                                    view.setTextColor(Color.parseColor(AppSharedPref.getAppThemeColor(view.context)))
                                } else {
                                    view.setTextColor(ContextCompat.getColor(view.context, android.R.color.white))
                                }
                            }
                        }
                    }
                }
            }catch (e: java.lang.Exception){
                e.printStackTrace()
            }
            }
        }

        /*@JvmStatic
        @BindingAdapter("appDrawableTin")
        fun setAppDrawableTin(view: View, drawableRes: Drawable) {
            if (ENABLE_DYNAMIC_THEME_COLOR) {
                *//*  if (view is ImageView && AppSharedPref.getButtonTextColor(view.context).isNotEmpty()) {
                      val wrappedDrawable: Drawable? = DrawableCompat.wrap(drawableRes)
                      wrappedDrawable?.let { DrawableCompat.setTint(it, Color.parseColor(AppSharedPref.getButtonTextColor(view.context))) }
                      view.background = wrappedDrawable
                  }*//*
            }
        }
*/
        /*
        *0==> Set color on whole lottie json.
        *
        * */
        @JvmStatic
        @BindingAdapter("lottieTint")
        fun lottieTint(view: LottieAnimationView, type: Int) {
            when (type) {
                0 -> {
                    if (AppSharedPref.getAppBgButtonColor(view.context).isNotEmpty()) {
                        view.addValueCallback(
                                KeyPath("**"),
                                LottieProperty.COLOR_FILTER,
                                { PorterDuffColorFilter(ColorUtils.setAlphaComponent(Color.parseColor(AppSharedPref.getAppBgButtonColor(view.context)), 180), PorterDuff.Mode.SRC_ATOP) }
                        )

                    }
                }
            }
        }

        @JvmStatic
        @BindingAdapter(value=["orderStatusBackground","orderStatusColor"], requireAll = false)
        fun setOrderStatusBackground(view: TextView, status: String?,statusColorCode:String?) {
            var drawable = ColorDrawable(Color.parseColor("#7986CB"))
            if (status != null) {
                when (status.lowercase()) {
                    ORDER_STATUS_DOWNLOADED ->{
                        drawable =if(statusColorCode.isNullOrEmpty())
                            ColorDrawable(ContextCompat.getColor(view.context, R.color.orderStatusDownloadedColor))
                        else  ColorDrawable(Color.parseColor(statusColorCode))
                    }
                    ORDER_STATUS_COMPLETE ->{
                        drawable =if(statusColorCode.isNullOrEmpty()) ColorDrawable(ContextCompat.getColor(view.context, R.color.orderStatusCompleteColor))
                        else  ColorDrawable(Color.parseColor(statusColorCode))
                    }

                    ORDER_STATUS_PENDING -> {
                        drawable = if(statusColorCode.isNullOrEmpty())  ColorDrawable(
                            ContextCompat.getColor(
                                view.context,
                                R.color.orderStatusPendingColor
                            )
                        )  else  ColorDrawable(Color.parseColor(statusColorCode))
                    }
                    ORDER_STATUS_PROCESSING ->{ drawable = if(statusColorCode.isNullOrEmpty()) ColorDrawable(ContextCompat.getColor(view.context, R.color.orderStatusProcessingColor))
                    else  ColorDrawable(Color.parseColor(statusColorCode))
                    }
                    ORDER_STATUS_HOLD ->{
                        drawable =  if(statusColorCode.isNullOrEmpty())  ColorDrawable(ContextCompat.getColor(view.context, R.color.orderStatusHoldColor))
                        else  ColorDrawable(Color.parseColor(statusColorCode))
                    }

                    ORDER_STATUS_CANCELLED ->{
                        drawable = if(statusColorCode.isNullOrEmpty())   ColorDrawable(ContextCompat.getColor(view.context, R.color.orderStatusCancelColor))
                        else  ColorDrawable(Color.parseColor(statusColorCode))
                    }

                    ORDER_STATUS_NEW ->{

                    drawable =  if(statusColorCode.isNullOrEmpty())  ColorDrawable(ContextCompat.getColor(view.context, R.color.orderStatusNewColor))
                    else  ColorDrawable(Color.parseColor(statusColorCode))
                    }
                    ORDER_STATUS_CLOSED ->{
                        drawable =  if(statusColorCode.isNullOrEmpty()) ColorDrawable(ContextCompat.getColor(view.context, R.color.orderStatusClosedColor))
                        else  ColorDrawable(Color.parseColor(statusColorCode))
                    }
                }
            }

            view.background = drawable
        }



        @JvmStatic
        @BindingAdapter("loadHtmlText")
        fun setLoadHtmlText(textView: TextView, htmlText: String?) {
            if (htmlText != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    textView.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    textView.text = Html.fromHtml(htmlText)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("loadWebViewData")
        fun setLoadData(webView: WebView, content: String?) {
            if (content == null) {
                return
            }

            val htmlContent = "<!DOCTYPE html>" +
                    "<html lang=\"en\">" +
                    "<head>" +
                    "<style>" +
                    "img{" +
                    "    height: auto;" +
                    "    max-width: 100%;" +
                    "}" +
                    "</style>" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
                    "<meta charset=\"utf-8\">" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + BASE_URL + "/pub/media/styles.css\" media=\"all\">" +
                    "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"" + BASE_URL + "/pub/static/version1552020129/_cache/merged/9e5d309101b494f03ad662f3809381e2.min.css\">" +
                    "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen and (min-width: 768px)\" href=\"" + BASE_URL + "/pub/static/version1552020129/frontend/Magento/luma/en_US/css/styles-l.min.css\">" +
                    "<script type=\"text/javascript\" src=\"" + BASE_URL + "/pub/static/version1552020129/_cache/merged/46c637c347e4d4a95871eb42fdf6692b.min.js\"></script>" +
                    "</head>" +
                    "<body data-container=\"body\" class=\"cms-privacy-policy-cookie-restriction-mode cms-page-view\">" +
                    "<div class=\"page-wrapper\">" +
                    "<main id=\"maincontent\" class=\"page-main\">" +
                    "<div class=\"columns\">" +
                    "<div class=\"column main\">" +
                    content +
                    "</div>" +
                    "</div>" +
                    "</main>" +
                    "</div>" +
                    "</body>" +
                    "</html>"

//            webView.loadData(htmlContent, "text/html; charset=utf-8", "UTF-8")
            webView.loadDataWithBaseURL("", htmlContent, "text/html; charset=utf-8", "UTF-8", "")

            val webSettings = webView.settings

            try {
                val nightModeFlags = webView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            webSettings.defaultFontSize = 14
            webSettings.defaultTextEncodingName = "utf-8"

            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    if (url.contains("goo.gl")) {
                        val gmmIntentUri = Uri.parse(url)
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(webView.context.packageManager) != null) {
                            webView.context.startActivity(mapIntent)
                        }
                        return true
                    } else {
                        CustomTabsHelper.openTab(webView.context, url)
                        return true
                    }

                }

                @TargetApi(Build.VERSION_CODES.N)
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    if (request.url.toString().contains("goo.gl")) {
                        val gmmIntentUri = Uri.parse(request.url.toString())
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(webView.context.packageManager) != null) {
                            webView.context.startActivity(mapIntent)
                        }
                        return true
                    } else {
                        CustomTabsHelper.openTab(webView.context, request.url.toString())
                        return true
                    }
                }
            }

            webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
                val request = DownloadManager.Request(Uri.parse(url))
                request.setMimeType(mimeType)
                request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url))
                request.addRequestHeader("User-Agent", userAgent)
                request.setDescription(webView.context.getString(R.string.download))
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
                request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalFilesDir(webView.context, Environment.DIRECTORY_DOWNLOADS, ".png")
                val dm = webView.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
                ToastHelper.showToast(webView.context, webView.context.getString(R.string.download_started))
            }

            webView.webChromeClient = WebChromeClient()

            LocaleUtils.updateConfig(webView.context)
        }


        @JvmStatic
        @BindingAdapter(value = ["loadWebViewOnBoardingData", "webBackgroundColor"], requireAll = false)
        fun setLoadOnBoardingData(webView: WebView, content: String?, webBackgroundColor: String?) {

            Log.d("Tag", "webBackgroundColor==>$webBackgroundColor")
            if (content == null) {
                return
            }

            val htmlContent = "<!DOCTYPE html>" +
                    "<html lang=\"en\">" +
                    "<head>" +
                    "<style>" +
                    "img{" +
                    "    height: auto;" +
                    "    max-width: 100%;" +
                    "}" +
                    "</style>" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
                    "<meta charset=\"utf-8\">" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + BASE_URL + "/pub/media/styles.css\" media=\"all\">" +
                    "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"" + BASE_URL + "/pub/static/version1552020129/_cache/merged/9e5d309101b494f03ad662f3809381e2.min.css\">" +
                    "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen and (min-width: 768px)\" href=\"" + BASE_URL + "/pub/static/version1552020129/frontend/Magento/luma/en_US/css/styles-l.min.css\">" +
                    "<script type=\"text/javascript\" src=\"" + BASE_URL + "/pub/static/version1552020129/_cache/merged/46c637c347e4d4a95871eb42fdf6692b.min.js\"></script>" +
                    "</head>" +
                    "<body " +
                    "style=\"background-color:$webBackgroundColor;\"" +
                    "data-container=\"body\" class=\"cms-privacy-policy-cookie-restriction-mode cms-page-view\">" +
                    "<div class=\"page-wrapper\">" +
                    "<main id=\"maincontent\" class=\"page-main\">" +
                    "<div class=\"columns\">" +
                    "<div " +
                    "class=\"column main\">" +
                    content +
                    "</div>" +
                    "</div>" +
                    "</main>" +
                    "</div>" +
                    "</body>" +
                    "</html>"

//            webView.loadData(htmlContent, "text/html; charset=utf-8", "UTF-8")
            webView.loadDataWithBaseURL("", htmlContent, "text/html; charset=utf-8", "UTF-8", "")

            val webSettings = webView.settings

            try {
                val nightModeFlags = webView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            webSettings.defaultFontSize = 14
            webSettings.defaultTextEncodingName = "utf-8"

            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    if (url.contains("goo.gl")) {
                        val gmmIntentUri = Uri.parse(url)
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(webView.context.packageManager) != null) {
                            webView.context.startActivity(mapIntent)
                        }
                        return true
                    } else {
                        CustomTabsHelper.openTab(webView.context, url)
                        return true
                    }

                }

                @TargetApi(Build.VERSION_CODES.N)
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    if (request.url.toString().contains("goo.gl")) {
                        val gmmIntentUri = Uri.parse(request.url.toString())
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(webView.context.packageManager) != null) {
                            webView.context.startActivity(mapIntent)
                        }
                        return true
                    } else {
                        CustomTabsHelper.openTab(webView.context, request.url.toString())
                        return true
                    }
                }
            }

            LocaleUtils.updateConfig(webView.context)
        }

        @JvmStatic
        @BindingAdapter("loadCmsData")
        fun setCmsData(webView: WebView, content: String?) {
            if (content == null) {
                return
            }


            val htmlContent = "<!DOCTYPE html>" +
                    "<html lang=\"en\">" +
                    "<head>" +
                    "<style>" +
                    "img{" +
                    "    height: auto;" +
                    "    max-width: 100%;" +
                    "}" +
                    "</style>" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
                    "<meta charset=\"utf-8\">" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + BASE_URL + "/pub/media/styles.css\" media=\"all\">" +
                    "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"" + BASE_URL + "/pub/static/version1552020129/_cache/merged/9e5d309101b494f03ad662f3809381e2.min.css\">" +
                    "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen and (min-width: 768px)\" href=\"" + BASE_URL + "/pub/static/version1552020129/frontend/Magento/luma/en_US/css/styles-l.min.css\">" +
                    "<script type=\"text/javascript\" src=\"" + BASE_URL + "/pub/static/version1552020129/_cache/merged/46c637c347e4d4a95871eb42fdf6692b.min.js\"></script>" +
                    "</head>" +
                    "<body data-container=\"body\" class=\"cms-privacy-policy-cookie-restriction-mode cms-page-view\" style=\"padding:20px 5px 5px 5px;\">" +
                    "<div class=\"page-wrapper\">" +
                    "<main id=\"maincontent\" class=\"page-main\">" +
                    "<div class=\"columns\">" +
                    "<div class=\"column main\">" +
                    content +
                    "</div>" +
                    "</div>" +
                    "</main>" +
                    "</div>" +
                    "</body>" +
                    "</html>"

//            webView.loadData(htmlContent, "text/html", "UTF-8")
            webView.loadDataWithBaseURL("", htmlContent, "text/html; charset=utf-8", "UTF-8", "")

            val webSettings = webView.settings

            try {
                val nightModeFlags = webView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            webSettings.defaultFontSize = 14

            LocaleUtils.updateConfig(webView.context)
        }

        @JvmStatic
        @BindingAdapter("swatchData")
        fun loadSwatchData(imageView: ImageView, swatchData: SwatchData) {
            if (swatchData.type == "1") {
                imageView.setBackgroundColor(Color.parseColor(swatchData.value))
            } else if (swatchData.type == "2") {
                ImageHelper.load(imageView, swatchData.value, null)
            }
        }

        @JvmStatic
        @BindingAdapter("ratingColor")
        fun setRatingColor(textView: AppCompatTextView, ratingValue: String?) {

            if (ratingValue != null && ratingValue.isNotBlank()) {
                when {
                    ratingValue.toDouble() < 2 -> textView.setBackgroundColor(ContextCompat.getColor(textView.context, R.color.single_star_color))
                    ratingValue.toDouble() < 3 -> textView.setBackgroundColor(ContextCompat.getColor(textView.context, R.color.two_star_color))
                    ratingValue.toDouble() < 4 -> textView.setBackgroundColor(ContextCompat.getColor(textView.context, R.color.three_star_color))
                    ratingValue.toDouble() < 5 -> textView.setBackgroundColor(ContextCompat.getColor(textView.context, R.color.four_star_color))
                    ratingValue.toDouble() == 5.toDouble() -> textView.setBackgroundColor(ContextCompat.getColor(textView.context, R.color.five_star_color))
                    else-> textView.setBackgroundColor(ContextCompat.getColor(textView.context, R.color.colorAccent))
                }
            }
        }

        @JvmStatic
        @BindingAdapter("hintWithAsterisk")
        fun setHintWithAsterisk(view: TextInputLayout, hint: String?) {
            val textSpannable = SpannableString("$hint*")
            textSpannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(view.context, R.color.text_color_secondary)), 0, textSpannable.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            textSpannable.setSpan(ForegroundColorSpan(Color.RED), textSpannable.length - 1, textSpannable.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            view.hint = textSpannable
        }

        @JvmStatic
        @BindingAdapter("textWithAsterisk")
        fun setTextWithAsterisk(view: AppCompatTextView, hint: String?) {
            val textSpannable = SpannableString("$hint*")
            textSpannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(view.context, R.color.text_color_secondary)), 0, textSpannable.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            textSpannable.setSpan(ForegroundColorSpan(Color.RED), textSpannable.length - 1, textSpannable.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            view.hint = textSpannable
        }


//        @JvmStatic
//        @BindingAdapter("circleTextDrawable")
//        fun setCircleTextDrawable(view: ImageView, text: String) {
//            val textDrawable = TextDrawable.builder()
//                    .beginConfig()
//                    .textColor(Color.WHITE)
//                    .toUpperCase()
//                    .endConfig()
//                    .buildRoundRect(text[0].toString().toUpperCase(Locale.getDefault()),
//                            ContextCompat.getColor(view.context, R.color.orderStatusNewColor),
//                            60)
//            view.setImageDrawable(textDrawable)
//        }


        @JvmStatic
        @BindingAdapter("walkThroughBackGround")
        fun setWalkThroughBackGroundColor(view: View, color: String) {
//            val nightModeFlags = view.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
//                view.background = ColorDrawable(ContextCompat.getColor(view.context, R.color.material_background))
//            } else {
            view.background = ColorDrawable(Color.parseColor(color))
//            }
        }

        @JvmStatic
        @BindingAdapter(value = ["walkThroughImageUrl", "walkThroughPlaceholder"], requireAll = false)
        fun setWalkThroughImageUrl(view: ImageView, imageUrl: String?, placeholder: String?) {
            if (placeholder.isNullOrBlank()) {
                Glide.with(view.context)
                        .load(imageUrl ?: "")
                        .thumbnail(0.01f)
                        .timeout(2 * 60000)
                        .apply(RequestOptions()
                                .placeholder(R.drawable.placeholder))
                        .into(view)

            } else {
                Glide.with(view.context)
                        .load(imageUrl ?: "")
                        .thumbnail(0.01f)
                        .timeout(2 * 60000)
                        .apply(RequestOptions()
                                .placeholder(ColorDrawable(Color.parseColor(placeholder))))
                        .into(view)

                //Color.parseColor(placeholder)
                //darken(placeholder,0.9)
            }
        }


    }
}