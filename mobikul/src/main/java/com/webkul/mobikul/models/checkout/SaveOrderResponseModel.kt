/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2018 Webkul Software Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.models.checkout


import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.BaseModel
import java.util.*


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class SaveOrderResponseModel() : BaseModel(), Parcelable {

    @JsonProperty("orderId")

    var orderId: String ?= ""
        get() = field?:""
    @JsonProperty("incrementId")

    var incrementId: String? = ""
        get() = field?:""
    @JsonProperty("canReorder")

    var canReorder: Boolean = false

    @JsonProperty("showCreateAccountLink")

    var showCreateAccountLink: Boolean = false

    @JsonProperty("email")

    var email: String ?= ""

    @JsonProperty("webview")

    var webview: Boolean = false

    @JsonProperty("redirectUrl")

    var redirectUrl: String ?= ""

    @JsonProperty("successUrl")

    var successUrl: ArrayList<String>? = ArrayList()

    @JsonProperty("cancelUrl")

    var cancelUrl: ArrayList<String>? = ArrayList()

    @JsonProperty("failureUrl")

    var failureUrl: ArrayList<String> ?= ArrayList()

    @JsonProperty("customerDetails")

    var customerDetails: CustomerDetails ?= CustomerDetails()

    constructor(parcel: Parcel) : this() {
        orderId = parcel.readString()
        incrementId = parcel.readString()
        canReorder = parcel.readByte() != 0.toByte()
        showCreateAccountLink = parcel.readByte() != 0.toByte()
        email = parcel.readString()
        webview = parcel.readByte() != 0.toByte()
        redirectUrl = parcel.readString()
        successUrl = parcel.createStringArrayList()
        cancelUrl = parcel.createStringArrayList()
        failureUrl = parcel.createStringArrayList()
        customerDetails = parcel.readParcelable(CustomerDetails::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderId)
        parcel.writeString(incrementId)
        parcel.writeByte(if (canReorder) 1 else 0)
        parcel.writeByte(if (showCreateAccountLink) 1 else 0)
        parcel.writeString(email)
        parcel.writeByte(if (webview) 1 else 0)
        parcel.writeString(redirectUrl)
        parcel.writeStringList(successUrl)
        parcel.writeStringList(cancelUrl)
        parcel.writeStringList(failureUrl)
        parcel.writeParcelable(customerDetails, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SaveOrderResponseModel> {
        override fun createFromParcel(parcel: Parcel): SaveOrderResponseModel {
            return SaveOrderResponseModel(parcel)
        }

        override fun newArray(size: Int): Array<SaveOrderResponseModel?> {
            return arrayOfNulls(size)
        }
    }
}