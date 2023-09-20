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

package com.webkul.mobikul.models.checkout


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.BR

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class BillingShippingAddress() : BaseObservable(), Parcelable {

    @JsonProperty("value")

    var value: String ?= ""
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.value)
        }

    @JsonProperty("id")

    var id: String ?= "0"

    var isSelected: Boolean = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

    constructor(parcel: Parcel) : this() {
        value = parcel.readString()
        id = parcel.readString()
        isSelected = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(value)
        parcel.writeString(id)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BillingShippingAddress> {
        override fun createFromParcel(parcel: Parcel): BillingShippingAddress {
            return BillingShippingAddress(parcel)
        }

        override fun newArray(size: Int): Array<BillingShippingAddress?> {
            return arrayOfNulls(size)
        }
    }
}