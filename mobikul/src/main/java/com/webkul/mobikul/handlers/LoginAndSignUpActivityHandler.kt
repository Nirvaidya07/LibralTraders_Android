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

package com.webkul.mobikul.handlers

import android.os.SystemClock
import com.webkul.mobikul.activities.LoginAndSignUpActivity
import com.webkul.mobikul.fragments.LoginBottomSheetFragment
import com.webkul.mobikul.fragments.SignUpBottomSheetFragment

class LoginAndSignUpActivityHandler(var mContext: LoginAndSignUpActivity) {

    private var mLastClickTime: Long = 0
    fun onClickLogin() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        val loginBottomSheetFragment = LoginBottomSheetFragment()
        loginBottomSheetFragment.show(mContext.supportFragmentManager, loginBottomSheetFragment.tag)
    }

    fun onClickSignUp() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        val signUpBottomSheetFragment = SignUpBottomSheetFragment()
        signUpBottomSheetFragment.show(mContext.supportFragmentManager, signUpBottomSheetFragment.tag)
    }
}