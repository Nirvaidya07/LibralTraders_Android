package com.webkul.mobikul.models.product

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.databinding.Bindable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ProductData() : Parcelable {

    @JsonProperty("sku")
    var sku: String? = ""

    @JsonProperty("name")
    var name: String? = ""
    @JsonProperty("productid")
    var productid: String? = ""
    @JsonProperty("price")
    var price: String? = ""

    @JsonProperty("product_type")
    var product_type: String? = ""

    @JsonProperty("stock_status")
    var stock_status: String? = ""

    @JsonProperty("qty_order")
    var qty_order: Int? = 1

    @JsonProperty("image")
    var image: String? = ""

    @JsonProperty("create_at")
    var create_at: String? = ""

    @JsonProperty("currency")
    var currency: String? = ""

    constructor(parcel: Parcel) : this() {
        sku = parcel.readString()
        name = parcel.readString()
        price = parcel.readString()
        product_type = parcel.readString()
        productid = parcel.readString()

        stock_status = parcel.readString()
        Log.d("TAG", "product: "+parcel.readString())
        qty_order = parcel.readInt()
        image = parcel.readString()
        create_at = parcel.readString()
        currency = parcel.readString()
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sku)
        parcel.writeString(name)
        parcel.writeString(price)
        parcel.writeString(product_type)
        parcel.writeString(stock_status)
        qty_order?.let { parcel.writeInt(it) }
        parcel.writeString(image)
        parcel.writeString(create_at)
        parcel.writeString(currency)
        parcel.writeString(productid)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "ProductData(sku=$sku, name=$name, productid=$productid, price=$price, product_type=$product_type, stock_status=$stock_status, qty_order=$qty_order, image=$image, create_at=$create_at, currency=$currency)"
    }


    companion object CREATOR : Parcelable.Creator<ProductData> {
        override fun createFromParcel(parcel: Parcel): ProductData {
            return ProductData(parcel)
        }

        override fun newArray(size: Int): Array<ProductData?> {
            return arrayOfNulls(size)
        }
    }

}