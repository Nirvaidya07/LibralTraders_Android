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

package com.webkul.mobikul.models.product

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.webkul.mobikul.BR


class SwatchData : BaseObservable() {

    var id: String = ""
    var type: String = ""
    var value: String = ""
    var position: Int = 0
    var isSelected: Boolean = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
    var isEnabled = true
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.enabled)
        }
}