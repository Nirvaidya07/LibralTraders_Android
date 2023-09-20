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

package com.webkul.mobikul.customviews

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.animation.OvershootInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.ablanco.zoomy.Zoomy
import com.webkul.mobikul.helpers.ApplicationConstants.ENABLE_IMAGE_ZOOMING


class ZoomImageView : AppCompatImageView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (ENABLE_IMAGE_ZOOMING) {
            val builder = Zoomy.Builder(context as Activity)
                    .target(this)
                    .interpolator(OvershootInterpolator())
                    .tapListener { super@ZoomImageView.callOnClick() }
                    .longPressListener { super@ZoomImageView.performLongClick() }
                    .doubleTapListener { super@ZoomImageView.callOnClick() }
            builder.register()
        }
    }
}