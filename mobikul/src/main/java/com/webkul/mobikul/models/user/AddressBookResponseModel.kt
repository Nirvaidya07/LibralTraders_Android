package com.webkul.mobikul.models.user


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.webkul.mobikul.models.BaseModel

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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class AddressBookResponseModel : BaseModel() {

    @JsonProperty("addressCount")

    var addressCount: Int = 0

    @JsonProperty("billingAddress")

    var billingAddress: AddressData = AddressData()

    @JsonProperty("shippingAddress")

    var shippingAddress: AddressData = AddressData()

    @JsonProperty("additionalAddress")

    var additionalAddress: ArrayList<AddressData> = ArrayList()
}