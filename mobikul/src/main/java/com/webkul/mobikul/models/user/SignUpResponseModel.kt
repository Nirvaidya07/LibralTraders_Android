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
class SignUpResponseModel : BaseModel() {

    @JsonProperty("customerName")

    var customerName: String = ""

    @JsonProperty("customerEmail")

    var customerEmail: String = ""

    @JsonProperty("customerToken")

    var customerToken: String = ""

    @JsonProperty("customerId")
    var customerId: String ?= ""

    @JsonProperty("profileImage")

    var profileImage: String = ""

    @JsonProperty("profileDominantColor")

    var profileDominantColor: String = ""

    @JsonProperty("bannerImage")

    var bannerImage: String = ""

    @JsonProperty("bannerDominantColor")

    var bannerDominantColor: String = ""

    /*---#MobikulMp--*/
    @JsonProperty("isSeller")
    var isSeller: Boolean = false

    @JsonProperty("isPending")
    var isPending: Boolean = false

    @JsonProperty("isAdmin")
    var isAdmin: Boolean = false
}