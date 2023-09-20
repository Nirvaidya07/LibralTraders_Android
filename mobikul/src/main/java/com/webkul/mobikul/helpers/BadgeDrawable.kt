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
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.webkul.mobikul.R
import io.github.inflationx.calligraphy3.TypefaceUtils



class BadgeDrawable() : Drawable(), Parcelable {

    private lateinit var mBadgePaint: Paint
    private lateinit var mBadgePaint1: Paint
    private lateinit var mTextPaint: Paint
    private val mTxtRect = Rect()

    private var mCount :String?= ""
    private var mCanDrawBadge: Boolean = false

    constructor(parcel: Parcel) : this() {
        mCount = parcel.readString()
        mCanDrawBadge = parcel.readByte() != 0.toByte()
    }

    constructor(context: Context) : this() {
        val mTextSize = context.resources.getDimension(R.dimen.badge_text_size)

        mBadgePaint = Paint()
        mBadgePaint.color = ContextCompat.getColor(context.applicationContext, R.color.color_whiteBlack)
        mBadgePaint.isAntiAlias = true
        mBadgePaint.style = Paint.Style.FILL

        mBadgePaint1 = Paint()
        mBadgePaint1.color = ContextCompat.getColor(context.applicationContext, R.color.text_color_secondary)
        mBadgePaint1.isAntiAlias = true
        mBadgePaint1.style = Paint.Style.FILL

        mTextPaint = Paint()
        mTextPaint.color = ContextCompat.getColor(context.applicationContext, R.color.text_color_secondary)
        mTextPaint.typeface = TypefaceUtils.load(context.assets, ApplicationConstants.CALLIGRAPHY_FONT_PATH_SEMI_BOLD)
        mTextPaint.textSize = mTextSize
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {

        if (!mCanDrawBadge) {
            return
        }
        val bounds = bounds
        val width = (bounds.right - bounds.left).toFloat()
        val height = (bounds.bottom - bounds.top).toFloat()

        // Position the badge in the top-right quadrant of the icon.
        /*Using Math.max rather than Math.min */

        val radius = Math.max(width, height) / 2 / 2
//        val centerX = width - radius - 1f + 20
        val centerY = radius - 5

        val left = (width / 2)
        val top = -10f
//        val right = width + 28f
        val bottom = height / 2

        // create a rectangle that we'll draw later
        if (mCount?.length ?: 0 <= 2) {
            val right = width + 10f
            // Draw badge circle.
            canvas.drawRoundRect(RectF(left, top, right, bottom), 5f, 5f, mBadgePaint1)
            canvas.drawRoundRect(RectF(left + 2, top + 2, right - 2, bottom - 2), 5f, 5f, mBadgePaint)
//            canvas.drawCircle(centerX, centerY, (radius + 7.5).toInt().toFloat(), mBadgePaint1)
//            canvas.drawCircle(centerX, centerY, (radius + 5.5).toInt().toFloat(), mBadgePaint)
        } else {
            val right = width + 30f
            canvas.drawRoundRect(RectF(left, top, right, bottom), 5f + 20, 5f, mBadgePaint1)
            canvas.drawRoundRect(RectF(left + 2, top + 2, right - 2, bottom - 2), 5f + 20, 5f, mBadgePaint)
//            canvas.drawCircle(centerX, centerY, (radius + 8.5).toInt().toFloat(), mBadgePaint1)
//            canvas.drawCircle(centerX, centerY, (radius + 6.5).toInt().toFloat(), mBadgePaint)
            //	        	canvas.drawRoundRect(radius, radius, radius, radius, 10, 10, mBadgePaint);
        }
        // Draw badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount?.length?:0, mTxtRect)
        val textHeight = (mTxtRect.bottom - mTxtRect.top).toFloat()
        val textY = centerY + textHeight / 2f
        if (mCount?.length ?:0  > 2) {
            canvas.drawText("99+", width - radius - 1f + 20, textY, mTextPaint)
        } else {
            mCount?.let { canvas.drawText(it, width - radius - 1f + 5, textY, mTextPaint) }
        }
    }

    /*
    Sets the count (i.e notifications) to display.
     */
    fun setCount(count: String) {
        mCount = count

        // Only draw a badge if there are notifications.
        mCanDrawBadge = !count.equals("0", ignoreCase = true)
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        // do nothing
    }

    override fun setColorFilter(cf: ColorFilter?) {
        // do nothing
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mCount)
        parcel.writeByte(if (mCanDrawBadge) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BadgeDrawable> {
        override fun createFromParcel(parcel: Parcel): BadgeDrawable {
            return BadgeDrawable(parcel)
        }

        override fun newArray(size: Int): Array<BadgeDrawable?> {
            return arrayOfNulls(size)
        }
    }
}