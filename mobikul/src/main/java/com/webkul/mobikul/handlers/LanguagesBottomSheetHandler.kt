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

import com.webkul.mobikul.fragments.LanguagesBottomSheetFragment

class LanguagesBottomSheetHandler(var mFragmentContext: LanguagesBottomSheetFragment) {

    fun onClickCancelBtn() {
        mFragmentContext.dismiss()
    }
}
