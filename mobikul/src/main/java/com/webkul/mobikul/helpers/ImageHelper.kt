package com.webkul.mobikul.helpers

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.webkul.mobikul.R

/**
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
class ImageHelper {

    companion object {

        @JvmStatic
        fun load(view: ImageView, imageUrl: String?, placeholder: String?) {
            if (placeholder.isNullOrBlank()) {
                Glide.with(view.context)
                        .load(imageUrl ?: "")
                        .thumbnail(0.1f)
                        .apply(RequestOptions()
                                .placeholder(R.drawable.placeholder)
                                .dontAnimate())
                        .into(view)
            } else {
                Glide.with(view.context)
                        .load(imageUrl ?: "")
                        .thumbnail(0.1f)
                        .apply(RequestOptions()
                                .placeholder(ColorDrawable(Color.parseColor(placeholder)))
                                .dontAnimate())
                        .into(view)
            }

            /* To remove all colors in offline mode */
//            val matrix = ColorMatrix()
//            if (NetworkHelper.isNetworkAvailable(view.context)) {
//                matrix.setSaturation(1f)
//            } else {
//                matrix.setSaturation(0f)
//            }
//            view.colorFilter = ColorMatrixColorFilter(matrix)
        }
    }
}