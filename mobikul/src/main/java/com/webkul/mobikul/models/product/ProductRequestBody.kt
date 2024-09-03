package com.webkul.mobikul.models.product

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ProductRequestBody() : Parcelable {

    @JsonProperty("pageSize")
    var pageSize: Int = 0
    @JsonProperty("currentPage")
    var currentPage: Int = 0
    @JsonProperty("customerId")
    var customerId: Int = 0
    constructor(parcel: Parcel) : this() {
        pageSize = parcel.readInt()
        currentPage = parcel.readInt()
        customerId = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(pageSize)
        parcel.writeInt(currentPage)
        parcel.writeInt(customerId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductRequestBody> {
        override fun createFromParcel(parcel: Parcel): ProductRequestBody {
            return ProductRequestBody(parcel)
        }

        override fun newArray(size: Int): Array<ProductRequestBody?> {
            return arrayOfNulls(size)
        }
    }
}