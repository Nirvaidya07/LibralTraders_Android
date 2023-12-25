/*
 * Webkul Software.
 *
 * Kotlin
 *
 * @author Webkul <support@webkul.com>
 * @category Webkul
 * @package com.webkul.mobikul
 * @copyright 2010-2019 Webkul Software Private Limited (https://webkul.com)
 * @license https://store.webkul.com/license.html ASL Licence
 * @link https://store.webkul.com/license.html
 */

package com.webkul.mobikul.models.user


import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.BaseModel

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class OrderDetailsModel() : BaseModel(), Parcelable {

    @JsonProperty("incrementId")
    var incrementId: String ?= ""

    @JsonProperty("shipmentId")
    var shipmentId: String ?= ""

    @JsonProperty("statusColorCode")
    var statusColorCode:String? = ""

 @JsonProperty("statusLabel")
    var statusLabel:String? = ""

    @JsonProperty("orderDate")
    var orderDate: String? = ""

    @JsonProperty("shippingAddress")
    var shippingAddress :String?= ""

    @JsonProperty("shippingMethod")
    var shippingMethod:String? = ""

    @JsonProperty("billingAddress")
    var billingAddress :String?= ""

    @JsonProperty("paymentMethod")
    var paymentMethod: String ?= ""

    @JsonProperty("orderData")
    var orderData: OrderData ?= OrderData()

    @JsonProperty("canReorder")

    var canReorder: Boolean = false

    @JsonProperty("state")
    var state: String ?= ""
        get() {
            return if (field == "new") "pending" else field
        }

    @JsonProperty("customerName")
    var customerName: String ?= ""

    @JsonProperty("customerEmail")
    var customerEmail: String ?= ""

    @JsonProperty("invoiceList")
    var invoiceList: ArrayList<InvoiceItem> ?= ArrayList()

    @JsonProperty("shipmentList")
    var shipmentList: ArrayList<ShipmentItem>? = ArrayList()

    @JsonProperty("creditmemoList")
    var creditMemoList: ArrayList<CreditMemoList>? = ArrayList()

    @JsonProperty("deliveryBoys")
    var deliveryBoys: ArrayList<DeliveryBoys>? = ArrayList()
    @JsonProperty("adminAddress")
    var adminAddress: String ?= ""

    constructor(parcel: Parcel) : this() {
        incrementId = parcel.readString()
        shipmentId = parcel.readString()
        statusLabel = parcel.readString()
        orderDate = parcel.readString()
        shippingAddress = parcel.readString()
        shippingMethod = parcel.readString()
        billingAddress = parcel.readString()
        paymentMethod = parcel.readString()
        orderData = parcel.readParcelable(OrderData::class.java.classLoader)
        canReorder = parcel.readByte() != 0.toByte()
        invoiceList = parcel.createTypedArrayList(InvoiceItem.CREATOR)
        shipmentList = parcel.createTypedArrayList(ShipmentItem.CREATOR)
        creditMemoList = parcel.createTypedArrayList(CreditMemoList.CREATOR)
        deliveryBoys=parcel.createTypedArrayList(DeliveryBoys.CREATOR)
        adminAddress = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(incrementId)
        parcel.writeString(shipmentId)
        parcel.writeString(statusLabel)
        parcel.writeString(orderDate)
        parcel.writeString(shippingAddress)
        parcel.writeString(shippingMethod)
        parcel.writeString(billingAddress)
        parcel.writeString(paymentMethod)
        parcel.writeParcelable(orderData, flags)
        parcel.writeByte(if (canReorder) 1 else 0)
        parcel.writeTypedList(invoiceList)
        parcel.writeTypedList(shipmentList)
        parcel.writeTypedList(creditMemoList)
        parcel.writeTypedList(deliveryBoys)
        parcel.writeString(adminAddress)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "OrderDetailsModel(incrementId=$incrementId, shipmentId=$shipmentId, statusColorCode=$statusColorCode, statusLabel=$statusLabel, orderDate=$orderDate, shippingAddress=$shippingAddress, shippingMethod=$shippingMethod, billingAddress=$billingAddress, paymentMethod=$paymentMethod, orderData=$orderData, canReorder=$canReorder, customerName=$customerName, customerEmail=$customerEmail, invoiceList=$invoiceList, shipmentList=$shipmentList, creditMemoList=$creditMemoList, deliveryBoys=$deliveryBoys, adminAddress=$adminAddress)"
    }

    companion object CREATOR : Parcelable.Creator<OrderDetailsModel> {
        override fun createFromParcel(parcel: Parcel): OrderDetailsModel {
            return OrderDetailsModel(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetailsModel?> {
            return arrayOfNulls(size)
        }
    }
}