package com.webkul.mobikul.helpers

import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.view.View
import android.view.ViewAnimationUtils
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener

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

class AnimationHelper {

    companion object {

        val ANIMATION_DURATION_SHORTEST = 150
        val ANIMATION_DURATION_SHORT = 250

        @TargetApi(21)
        fun circleRevealView(view: View) {
            circleRevealView(view, ANIMATION_DURATION_SHORT)
        }

        @TargetApi(21)
        fun circleRevealView(view: View, duration: Int) {
            // get the center for the clipping circle
            val cx = view.width
            val cy = view.height / 2

            // get the final radius for the clipping circle
            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

            // create the animator for this view (the start radius is zero)
            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)

            if (duration > 0) {
                anim.duration = duration.toLong()
            } else {
                anim.duration = ANIMATION_DURATION_SHORT.toLong()
            }

            // make the view visible and start the animation
            view.visibility = View.VISIBLE
            anim.start()
        }

        @TargetApi(21)
        fun circleHideView(view: View, listenerAdapter: AnimatorListenerAdapter) {
            circleHideView(view, ANIMATION_DURATION_SHORT, listenerAdapter)
        }

        @TargetApi(21)
        private fun circleHideView(view: View, duration: Int, listenerAdapter: AnimatorListenerAdapter) {
            // get the center for the clipping circle
            val cx = view.width
            val cy = view.height / 2

            // get the initial radius for the clipping circle
            val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

            // create the animation (the final radius is zero)
            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0f)

            // make the view invisible when the animation is done
            anim.addListener(listenerAdapter)

            if (duration > 0) {
                anim.duration = duration.toLong()
            } else {
                anim.duration = ANIMATION_DURATION_SHORT.toLong()
            }

            //        anim.setStartDelay(200);

            // start the animation
            anim.start()
        }

        fun fadeInView(view: View) {
            fadeInView(view, ANIMATION_DURATION_SHORTEST)
        }

        /**
         * Reveal the provided View with a fade-in animation.
         *
         * @param view     The View that's being animated.
         * @param duration How long should the animation take, in millis.
         */
        private fun fadeInView(view: View, duration: Int) {
            view.visibility = View.VISIBLE
            view.alpha = 0f

            // Setting the listener to null, so it won't keep getting called.
            ViewCompat.animate(view).alpha(1f).setDuration(duration.toLong()).setListener(null)
        }

        /**
         * Hide the provided View with a fade-out animation. Fast.
         *
         * @param view The View that's being animated.
         */
        fun fadeOutView(view: View) {
            fadeOutView(view, ANIMATION_DURATION_SHORTEST)
        }

        /**
         * Hide the provided View with a fade-out animation.
         *
         * @param view     The View that's being animated.
         * @param duration How long should the animation take, in millis.
         */
        private fun fadeOutView(view: View, duration: Int) {
            ViewCompat.animate(view).alpha(0f).setDuration(duration.toLong()).setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View) {
                    view.isDrawingCacheEnabled = true
                }

                override fun onAnimationEnd(view: View) {
                    view.visibility = View.GONE
                    view.alpha = 1f
                    view.isDrawingCacheEnabled = false
                }

                override fun onAnimationCancel(view: View) {}
            })
        }
    }
}