package com.webkul.mobikul.models.checkout

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class CustomerDetails() : Parcelable {

    @JsonProperty("prefix")

    var prefix: String ?= ""

    @JsonProperty("firstname")

    var firstname: String ?= ""

    @JsonProperty("lastname")

    var lastname: String ?= ""

    @JsonProperty("suffix")

    var suffix: String ?= ""

    @JsonProperty("email")

    var email: String ?= ""

    constructor(parcel: Parcel) : this() {
        prefix = parcel.readString()
        firstname = parcel.readString()
        lastname = parcel.readString()
        suffix = parcel.readString()
        email = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(prefix)
        parcel.writeString(firstname)
        parcel.writeString(lastname)
        parcel.writeString(suffix)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomerDetails> {
        override fun createFromParcel(parcel: Parcel): CustomerDetails {
            return CustomerDetails(parcel)
        }

        override fun newArray(size: Int): Array<CustomerDetails?> {
            return arrayOfNulls(size)
        }
    }
}